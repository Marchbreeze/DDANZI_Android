package co.orange.sell.confirm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.orange.core.state.UiState
import co.orange.domain.entity.response.SellBuyerInfoModel
import co.orange.domain.usecase.sell.ConfirmSellOrderUseCase
import co.orange.domain.usecase.sell.GetBuyerInfoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SellConfirmViewModel
@Inject
constructor(
    private val getBuyerInfoUseCase: GetBuyerInfoUseCase,
    private val confirmSellOrderUseCase: ConfirmSellOrderUseCase
) : ViewModel() {
    var orderId = ""
    var totalPrice = 0

    private val _getBuyerInfoState = MutableStateFlow<UiState<SellBuyerInfoModel>>(UiState.Empty)
    val getBuyerInfoState: StateFlow<UiState<SellBuyerInfoModel>> = _getBuyerInfoState

    private val _patchOrderConfirmState = MutableStateFlow<UiState<Boolean>>(UiState.Empty)
    val patchOrderConfirmState: StateFlow<UiState<Boolean>> = _patchOrderConfirmState

    fun getBuyerInfoFromServer() {
        _getBuyerInfoState.value = UiState.Loading
        viewModelScope.launch {
            getBuyerInfoUseCase(orderId)
                .onSuccess {
                    _getBuyerInfoState.value = UiState.Success(it)
                }.onFailure {
                    _getBuyerInfoState.value = UiState.Failure(it.message.orEmpty())
                }
        }
    }

    fun patchOrderConfirmToServer() {
        _patchOrderConfirmState.value = UiState.Loading
        viewModelScope.launch {
            confirmSellOrderUseCase(orderId)
                .onSuccess {
                    _patchOrderConfirmState.value = UiState.Success(true)
                }
                .onFailure {
                    _patchOrderConfirmState.value = UiState.Failure(it.message.orEmpty())
                }
        }
    }
}
