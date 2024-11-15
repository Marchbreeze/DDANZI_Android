package co.orange.buy.progress

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.orange.buy.BuildConfig.PAYMENT_UID
import co.orange.core.amplitude.AmplitudeManager
import co.orange.core.state.UiState
import co.orange.domain.entity.response.BuyProgressModel
import co.orange.domain.entity.response.PayEndModel
import co.orange.domain.entity.response.PayStartModel
import co.orange.domain.enums.PaymentMethod
import co.orange.domain.repository.BuyRepository
import co.orange.domain.usecase.BuyGetPurchaseProgressDataUseCase
import co.orange.domain.usecase.BuyRequestProductOrderUseCase
import co.orange.domain.usecase.BuySetPayFinishedUseCase
import co.orange.domain.usecase.BuySetPayStartedUseCase
import com.iamport.sdk.data.sdk.IamPortRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class BuyProgressViewModel
@Inject
constructor(
    private val buyProgressUseCase: BuyGetPurchaseProgressDataUseCase,
    private val buySetPayStartedUseCase: BuySetPayStartedUseCase,
    private val buySetPayFinishedUseCase: BuySetPayFinishedUseCase,
    private val buyRequestProductOrderUseCase: BuyRequestProductOrderUseCase
) : ViewModel() {
    var productId: String = ""
    private var orderId: String = ""
    var totalPrice = 0

    private var buyProgressData: BuyProgressModel? = null
    var optionList = listOf<Long>()

    var payMethodId = MutableLiveData<Int>(-1)
    private var payMethod = ""

    var isTermAllSelected = MutableLiveData<Boolean>(false)
    var isTermServiceSelected = MutableLiveData<Boolean>(false)
    var isTermPurchaseSelected = MutableLiveData<Boolean>(false)
    var isCompleted = MutableLiveData<Boolean>(false)
    var isOrderStarted = false

    private val _getBuyDataState = MutableStateFlow<UiState<BuyProgressModel>>(UiState.Empty)
    val getBuyDataState: StateFlow<UiState<BuyProgressModel>> = _getBuyDataState

    private val _postPayStartState = MutableStateFlow<UiState<PayStartModel>>(UiState.Empty)
    val postPayStartState: StateFlow<UiState<PayStartModel>> = _postPayStartState

    private val _patchPayEndState = MutableStateFlow<UiState<PayEndModel>>(UiState.Empty)
    val patchPayEndState: StateFlow<UiState<PayEndModel>> = _patchPayEndState

    private val _postOrderState = MutableStateFlow<UiState<String>>(UiState.Empty)
    val postOrderState: StateFlow<UiState<String>> = _postOrderState

    fun checkAllTerm() {
        AmplitudeManager.trackEvent("click_purchase_terms_all")
        isTermServiceSelected.value = isTermAllSelected.value?.not()
        isTermPurchaseSelected.value = isTermAllSelected.value?.not()
        isTermAllSelected.value = isTermAllSelected.value?.not()
        checkIsCompleted()
    }

    fun checkServiceTerm() {
        isTermServiceSelected.value = isTermServiceSelected.value?.not()
        checkIsCompleted()
    }

    fun checkPurchaseTerm() {
        isTermPurchaseSelected.value = isTermPurchaseSelected.value?.not()
        checkIsCompleted()
    }

    fun setPayMethod(methodId: Int) {
        payMethodId.value = methodId
        payMethod = PaymentMethod.fromId(methodId)?.methodName ?: PAYMENT_DEFAULT
        checkIsCompleted()
    }

    private fun checkIsCompleted() {
        isTermAllSelected.value =
            (isTermServiceSelected.value == true && isTermPurchaseSelected.value == true)
        isCompleted.value =
            (isTermAllSelected.value == true && !buyProgressData?.addressInfo?.address.isNullOrBlank() && payMethod.isNotBlank())
    }

    fun getBuyDataFromServer() {
        _getBuyDataState.value = UiState.Loading
        viewModelScope.launch {
            buyProgressUseCase(productId)
                .onSuccess {
                    checkIsCompleted()
                    buyProgressData = it
                    totalPrice = it.totalPrice
                    _getBuyDataState.value = UiState.Success(it)
                }
                .onFailure {
                    _getBuyDataState.value = UiState.Failure(it.message.toString())
                }
        }
    }

    fun postPayStartToServer() {
        isOrderStarted = true
        _postPayStartState.value = UiState.Loading
        viewModelScope.launch {
            buySetPayStartedUseCase(buyProgressData, payMethod)
                .onSuccess {
                    orderId = it.orderId
                    _postPayStartState.value = UiState.Success(it)
                }.onFailure {
                    _postPayStartState.value = UiState.Failure(it.message.orEmpty())
                }
        }
    }

    fun createIamportRequest(): IamPortRequest? =
        if (buyProgressData?.productName.isNullOrBlank() || payMethod.isBlank() || orderId.isBlank()) {
            Timber.tag("okhttp").d("IAMPORT PURCHASE REQUEST ERROR : $buyProgressData")
            null
        } else {
            IamPortRequest(
                pg = NICE_PAYMENTS,
                pay_method = payMethod,
                name = buyProgressData?.modifiedProductName,
                merchant_uid = orderId,
                amount = buyProgressData?.totalPrice.toString(),
                buyer_name = buyProgressData?.addressInfo?.recipient,
                buyer_tel = buyProgressData?.addressInfo?.recipientPhone,
                digital = false,
            )
        }

    fun patchPayEndToServer(isSuccess: Boolean?) {
        _patchPayEndState.value = UiState.Loading
        viewModelScope.launch {
            buySetPayFinishedUseCase(orderId, isSuccess)
                .onSuccess {
                    _patchPayEndState.value = UiState.Success(it)
                }.onFailure {
                    _patchPayEndState.value = UiState.Failure(it.message.orEmpty())
                }
        }
    }

    fun postToRequestOrderToServer() {
        _postOrderState.value = UiState.Loading
        viewModelScope.launch {
            buyRequestProductOrderUseCase(orderId, optionList)
                .onSuccess {
                    AmplitudeManager.apply {
                        plusIntProperty("user_purchase_count", 1)
                        plusIntProperty("user_purchase_total", totalPrice)
                    }
                    _postOrderState.value = UiState.Success(it.orderId)
                }.onFailure {
                    _postOrderState.value = UiState.Failure(it.message.orEmpty())
                }
        }
    }

    companion object {
        private const val NICE_PAYMENTS = "nice_v2.$PAYMENT_UID"

        private const val PAYMENT_DEFAULT = "card"
    }
}
