package co.orange.setting.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.orange.core.state.UiState
import co.orange.domain.entity.response.HistoryBuyModel
import co.orange.domain.entity.response.HistoryInterestModel
import co.orange.domain.entity.response.HistorySellModel
import co.orange.domain.usecase.history.GetBuyItemListUseCase
import co.orange.domain.usecase.history.GetInterestItemListUseCase
import co.orange.domain.usecase.history.GetSellItemListUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel
@Inject
constructor(
    private val getBuyItemListUseCase: GetBuyItemListUseCase,
    private val getSellItemListUseCase: GetSellItemListUseCase,
    private val getInterestItemListUseCase: GetInterestItemListUseCase,
) : ViewModel() {
    var currentType: Int = -1

    private val _getBuyListState =
        MutableStateFlow<UiState<HistoryBuyModel>>(UiState.Empty)
    val getBuyListState: StateFlow<UiState<HistoryBuyModel>> = _getBuyListState

    private val _getSellListState =
        MutableStateFlow<UiState<HistorySellModel>>(UiState.Empty)
    val getSellListState: StateFlow<UiState<HistorySellModel>> = _getSellListState

    private val _getInterestListState =
        MutableStateFlow<UiState<HistoryInterestModel>>(UiState.Empty)
    val getInterestListState: StateFlow<UiState<HistoryInterestModel>> = _getInterestListState

    fun getBuyListFromServer() {
        _getBuyListState.value = UiState.Loading
        viewModelScope.launch {
            getBuyItemListUseCase()
                .onSuccess {
                    _getBuyListState.value = UiState.Success(it)
                }
                .onFailure {
                    _getBuyListState.value = UiState.Failure(it.message.toString())
                }
        }
    }

    fun getSellListFromServer() {
        _getSellListState.value = UiState.Loading
        viewModelScope.launch {
            getSellItemListUseCase()
                .onSuccess {
                    _getSellListState.value = UiState.Success(it)
                }
                .onFailure {
                    _getSellListState.value = UiState.Failure(it.message.toString())
                }
        }
    }

    fun getInterestListFromServer() {
        _getInterestListState.value = UiState.Loading
        viewModelScope.launch {
            getInterestItemListUseCase()
                .onSuccess {
                    _getInterestListState.value = UiState.Success(it)
                }
                .onFailure {
                    _getInterestListState.value = UiState.Failure(it.message.toString())
                }
        }
    }
}
