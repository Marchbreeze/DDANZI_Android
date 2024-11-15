package co.orange.buy.info

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.orange.core.state.UiState
import co.orange.domain.entity.response.OrderInfoModel
import co.orange.domain.usecase.BuyConfirmOrderUseCase
import co.orange.domain.usecase.BuyGetOrderInfoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BuyInfoViewModel
@Inject
constructor(
    private val buyGetOrderInfoUseCase: BuyGetOrderInfoUseCase,
    private val buyConfirmOrderUseCase: BuyConfirmOrderUseCase
) : ViewModel() {
    var orderId: String = ""

    private val _getOrderInfoState = MutableStateFlow<UiState<OrderInfoModel>>(UiState.Empty)
    val getOrderInfoState: StateFlow<UiState<OrderInfoModel>> = _getOrderInfoState

    private val _patchOrderConfirmResult = MutableSharedFlow<Boolean>()
    val patchOrderConfirmResult: SharedFlow<Boolean> = _patchOrderConfirmResult

    fun getOrderInfoFromServer() {
        _getOrderInfoState.value = UiState.Loading
        viewModelScope.launch {
            buyGetOrderInfoUseCase(orderId)
                .onSuccess {
                    _getOrderInfoState.value = UiState.Success(it)
                }
                .onFailure {
                    _getOrderInfoState.value = UiState.Failure(it.message.orEmpty())
                }
        }
    }

    fun patchOrderConfirmToServer() {
        viewModelScope.launch {
            buyConfirmOrderUseCase(orderId)
                .onSuccess {
                    _patchOrderConfirmResult.emit(true)
                }
                .onFailure {
                    _patchOrderConfirmResult.emit(false)
                }
        }
    }
}
