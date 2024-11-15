package co.orange.main.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.orange.core.state.UiState
import co.orange.domain.entity.response.SearchInfoModel
import co.orange.domain.entity.response.SearchResultModel
import co.orange.domain.repository.UserRepository
import co.orange.domain.usecase.home.SetInterestStateUseCase
import co.orange.domain.usecase.search.GetRecommendSearchUseCase
import co.orange.domain.usecase.search.GetSearchedResultUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel
@Inject
constructor(
    private val getRecommendSearchUseCase: GetRecommendSearchUseCase,
    private val getSearchedResultUseCase: GetSearchedResultUseCase,
    private val setInterestStateUseCase: SetInterestStateUseCase,
    private val userRepository: UserRepository,
) : ViewModel() {
    var currentKeyword = ""

    private val _getSearchInfoState = MutableStateFlow<UiState<SearchInfoModel>>(UiState.Empty)
    val getSearchInfoState: StateFlow<UiState<SearchInfoModel>> = _getSearchInfoState

    private val _getSearchResultState = MutableStateFlow<UiState<SearchResultModel>>(UiState.Empty)
    val getSearchResultState: StateFlow<UiState<SearchResultModel>> = _getSearchResultState

    private val _likeState = MutableStateFlow<Boolean?>(null)
    val likeState: StateFlow<Boolean?> = _likeState

    var likedPosition = -1

    fun getSearchInfoFromServer() {
        _getSearchInfoState.value = UiState.Loading
        viewModelScope.launch {
            getRecommendSearchUseCase()
                .onSuccess {
                    _getSearchInfoState.value = UiState.Success(it)
                }
                .onFailure {
                    _getSearchInfoState.value = UiState.Failure(it.message.toString())
                }
        }
    }

    fun getSearchResultFromServer(keyword: String) {
        _getSearchResultState.value = UiState.Loading
        currentKeyword = keyword
        viewModelScope.launch {
            getSearchedResultUseCase(keyword)
                .onSuccess {
                    _getSearchResultState.value = UiState.Success(it)
                }
                .onFailure {
                    _getSearchResultState.value = UiState.Failure(it.message.toString())
                }
        }
    }

    fun setLikeStateWithServer(
        productId: String,
        isInterested: Boolean,
        position: Int,
    ) {
        viewModelScope.launch {
            likedPosition = position
            setInterestStateUseCase(isInterested, productId)
                .onSuccess {
                    _likeState.value = it
                    _likeState.value = null
                }
        }
    }

    fun getUserLogined() = userRepository.getUserRegistered()
}
