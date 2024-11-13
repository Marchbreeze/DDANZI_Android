package co.orange.auth.phone

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.orange.core.amplitude.AmplitudeManager
import co.orange.core.extension.toPhoneFrom
import co.orange.core.state.UiState
import co.orange.domain.entity.request.SignUpRequestModel
import co.orange.domain.entity.response.IamportCertificationModel
import co.orange.domain.repository.AuthRepository
import co.orange.domain.repository.IamportRepository
import co.orange.domain.repository.UserRepository
import co.orange.domain.usecase.IamportGetDataUseCase
import co.orange.domain.usecase.IamportGetTokenUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.Period
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class PhoneViewModel
@Inject
constructor(
    private val authRepository: AuthRepository,
    private val iamportRepository: IamportRepository,
    private val userRepository: UserRepository,
    private val iamportGetTokenUseCase: IamportGetTokenUseCase,
    private val iamportGetDataUseCase: IamportGetDataUseCase,
) : ViewModel() {
    var certificatedUid: String = ""

    var isTermAllSelected = MutableLiveData<Boolean>(false)
    var isTermPrivateSelected = MutableLiveData<Boolean>(false)
    var isTermServiceSelected = MutableLiveData<Boolean>(false)
    var isTermMarketingSelected = MutableLiveData<Boolean>(false)
    var isCompleted = MutableLiveData<Boolean>(false)

    private val _isSubmitClicked = MutableSharedFlow<Boolean>()
    val isSubmitClicked: SharedFlow<Boolean> = _isSubmitClicked

    private val _getIamportDataResult = MutableSharedFlow<Boolean>()
    val getIamportDataResult: SharedFlow<Boolean> = _getIamportDataResult

    private val _postSignUpState = MutableStateFlow<UiState<String>>(UiState.Empty)
    val postSignUpState: StateFlow<UiState<String>> = _postSignUpState

    fun checkAllTerm() {
        AmplitudeManager.trackEvent("click_verification_terms_all")
        isTermPrivateSelected.value = isTermAllSelected.value?.not()
        isTermServiceSelected.value = isTermAllSelected.value?.not()
        isTermMarketingSelected.value = isTermAllSelected.value?.not()
        isTermAllSelected.value = isTermAllSelected.value?.not()
        checkIsCompleted()
    }

    fun checkPrivateTerm() {
        isTermPrivateSelected.value = isTermPrivateSelected.value?.not()
        checkIsCompleted()
    }

    fun checkServiceTerm() {
        isTermServiceSelected.value = isTermServiceSelected.value?.not()
        checkIsCompleted()
    }

    fun checkMarketingTerm() {
        isTermMarketingSelected.value = isTermMarketingSelected.value?.not()
        checkIsCompleted()
    }

    private fun checkIsCompleted() {
        isCompleted.value =
            (isTermPrivateSelected.value == true && isTermServiceSelected.value == true)
        isTermAllSelected.value =
            (isTermPrivateSelected.value == true && isTermServiceSelected.value == true && isTermMarketingSelected.value == true)
    }

    fun clickSubmitBtn(boolean: Boolean) {
        viewModelScope.launch {
            _isSubmitClicked.emit(boolean)
        }
    }

    fun postToGetIamportTokenFromServer() {
        viewModelScope.launch {
            iamportGetTokenUseCase(certificatedUid)
                .onSuccess { token ->
                    getCertificationDataFromServer(token)
                }
                .onFailure {
                    _getIamportDataResult.emit(false)
                }
        }
    }

    private fun getCertificationDataFromServer(accessToken: String) {
        viewModelScope.launch {
            iamportGetDataUseCase(accessToken, certificatedUid)
                .onSuccess { response ->
                    _getIamportDataResult.emit(true)
                    postToSignUpFromServer(
                        SignUpRequestModel(
                            name = response.name.orEmpty(),
                            phone = response.phone.orEmpty(),
                            birth = response.birthday.orEmpty(),
                            sex = response.gender?.uppercase().orEmpty(),
                            isAgreedMarketingTerm = isTermMarketingSelected.value ?: false,
                            ci = response.uniqueKey.orEmpty(),
                        ),
                    )
                    userRepository.setUserInfo(
                        userName = response.name.orEmpty(),
                        userPhone = response.phone?.toPhoneFrom().orEmpty(),
                    )
                    setAmplitudeUserProperty(response)
                }
                .onFailure {
                    _getIamportDataResult.emit(false)
                }
        }
    }

    private fun postToSignUpFromServer(request: SignUpRequestModel) {
        viewModelScope.launch {
            authRepository.postToSignUp(userRepository.getAccessToken(), request)
                .onSuccess {
                    userRepository.setUserStatus(it.status)
                    _postSignUpState.value = UiState.Success(it.nickname)
                }
                .onFailure {
                    _postSignUpState.value = UiState.Failure(it.message.orEmpty())
                }
        }
    }

    private fun setAmplitudeUserProperty(item: IamportCertificationModel) {
        AmplitudeManager.apply {
            updateProperty("user_sex", item.gender?.uppercase().orEmpty())
            updateProperty("user_name", item.name.orEmpty())
            item.birthday?.let { birthday ->
                updateProperty("user_birthday", birthday.convertOnlyDate())
                updateIntProperty("user_age", birthday.convertToAge())
            }
            updateProperty(
                "user_signup_date",
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
            )
            updateIntProperty("user_purchase_count", 0)
            updateIntProperty("user_purchase_total", 0)
            updateIntProperty("user_selling_try", 0)
            updateIntProperty("user_selling_count", 0)
            updateIntProperty("user_selling_total", 0)
        }
    }

    private fun String.convertOnlyDate(): String =
        LocalDate.parse(this, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
            .format(DateTimeFormatter.ofPattern("MMdd"))

    private fun String.convertToAge(): Int =
        Period.between(
            LocalDate.parse(this, DateTimeFormatter.ofPattern("yyyy-MM-dd")),
            LocalDate.now(),
        ).years + 1
}
