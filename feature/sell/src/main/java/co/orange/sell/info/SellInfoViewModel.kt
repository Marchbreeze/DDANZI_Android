package co.orange.sell.info

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.orange.core.state.UiState
import co.orange.domain.entity.response.SellInfoModel
import co.orange.domain.usecase.sell.DeleteSellingItemUseCase
import co.orange.domain.usecase.sell.GetSellOrderInfoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SellInfoViewModel
@Inject
constructor(
    private val getSellOrderInfoUseCase: GetSellOrderInfoUseCase,
    private val deleteSellingItemUseCase: DeleteSellingItemUseCase,
) : ViewModel() {
    var itemId = ""
    var orderId = ""
    var totalPrice = 0

    private val _getSellInfoState = MutableStateFlow<UiState<SellInfoModel>>(UiState.Empty)
    val getSellInfoState: StateFlow<UiState<SellInfoModel>> = _getSellInfoState

    private val _deleteItemState = MutableStateFlow<UiState<Boolean>>(UiState.Empty)
    val deleteItemState: StateFlow<UiState<Boolean>> = _deleteItemState

    fun getItemDetailInfoFromServer() {
        _getSellInfoState.value = UiState.Loading
        viewModelScope.launch {
            getSellOrderInfoUseCase(itemId)
                .onSuccess {
                    orderId = it.orderId.orEmpty()
                    totalPrice = it.salePrice
                    _getSellInfoState.value = UiState.Success(it)
                }.onFailure {
                    _getSellInfoState.value = UiState.Failure(it.message.orEmpty())
                }
        }
    }

    fun deleteSellingItemFromServer() {
        _deleteItemState.value = UiState.Loading
        viewModelScope.launch {
            deleteSellingItemUseCase(itemId)
                .onSuccess {
                    _deleteItemState.value = UiState.Success(it)
                }.onFailure {
                    _deleteItemState.value = UiState.Failure(it.message.orEmpty())
                }
        }
    }
}
