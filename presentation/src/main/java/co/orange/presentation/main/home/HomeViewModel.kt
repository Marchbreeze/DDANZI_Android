package co.orange.presentation.main.home

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.orange.core.state.UiState
import co.orange.domain.entity.response.ProductModel
import co.orange.domain.entity.response.ProductModel.Companion.imageOnlyProductModel
import co.orange.domain.repository.HomeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel
    @Inject
    constructor(
        private val homeRepository: HomeRepository,
    ) : ViewModel() {
        init {
            getHomeDataFromServer()
        }

        var selectedImageUri = Uri.EMPTY

        private val _isCheckedAgain = MutableSharedFlow<Boolean>()
        val isCheckedAgain: SharedFlow<Boolean> = _isCheckedAgain

        private val _getHomeDataState = MutableStateFlow<UiState<List<ProductModel>>>(UiState.Empty)
        val getHomeDataState: StateFlow<UiState<List<ProductModel>>> = _getHomeDataState

        fun setCheckedState(state: Boolean) {
            viewModelScope.launch {
                _isCheckedAgain.emit(state)
            }
        }

        private fun getHomeDataFromServer() {
            viewModelScope.launch {
                homeRepository.getHomeData()
                    .onSuccess {
                        val itemList = it.productList.toMutableList()
                        itemList.add(
                            0,
                            imageOnlyProductModel(it.homeImgUrl),
                        )
                        _getHomeDataState.value = UiState.Success(itemList)
                    }
                    .onFailure {
                        _getHomeDataState.value = UiState.Failure(it.message.toString())
                    }
            }
        }
    }
