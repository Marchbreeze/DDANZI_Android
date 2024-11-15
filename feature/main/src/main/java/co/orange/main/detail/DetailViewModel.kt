package co.orange.main.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.orange.core.state.UiState
import co.orange.domain.entity.response.ProductDetailModel
import co.orange.domain.entity.response.ProductOptionModel
import co.orange.domain.repository.UserRepository
import co.orange.domain.usecase.home.GetProductDetailUseCase
import co.orange.domain.usecase.home.SetInterestStateUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailViewModel
@Inject
constructor(
    private val getProductDetailUseCase: GetProductDetailUseCase,
    private val setInterestStateUseCase: SetInterestStateUseCase,
    private val userRepository: UserRepository,
) : ViewModel() {
    var productId: String = ""

    var infoUrl: String = ""
    var interestCount: Int = 0
    var optionList = listOf<ProductOptionModel>()
    var selectedOptionList = arrayListOf<Int>()

    private val _getProductDetailState =
        MutableStateFlow<UiState<ProductDetailModel>>(UiState.Empty)
    val getProductDetailState: StateFlow<UiState<ProductDetailModel>> = _getProductDetailState

    private val _likeState = MutableStateFlow<Boolean>(false)
    val likeState: StateFlow<Boolean> = _likeState

    var originLikeState = false

    fun getProductDetailFromServer() {
        viewModelScope.launch {
            getProductDetailUseCase(productId)
                .onSuccess {
                    infoUrl = it.infoUrl
                    interestCount = it.interestCount
                    optionList = it.optionList
                    selectedOptionList =
                        ArrayList<Int>(optionList.size).apply {
                            repeat(optionList.size) { add(-1) }
                        }
                    originLikeState = it.isInterested
                    _likeState.value = it.isInterested
                    _getProductDetailState.value = UiState.Success(it)
                }
                .onFailure {
                    _getProductDetailState.value = UiState.Failure(it.message.toString())
                }
        }
    }

    fun setLikeStateWithServer() {
        viewModelScope.launch {
            setInterestStateUseCase(likeState.value, productId)
                .onSuccess {
                    interestCount = if (it) interestCount + 1 else interestCount - 1
                    _likeState.value = it
                }
        }
    }

    fun getUserLogined() = userRepository.getUserRegistered()
}
