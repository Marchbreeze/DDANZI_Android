package co.orange.main.main.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.orange.core.state.UiState
import co.orange.domain.entity.response.HomeModel
import co.orange.domain.repository.UserRepository
import co.orange.domain.usecase.home.GetHomeListByPageUseCase
import co.orange.domain.usecase.home.SetInterestStateUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.ceil

@HiltViewModel
class HomeViewModel
@Inject
constructor(
    private val getHomeListByPageUseCase: GetHomeListByPageUseCase,
    private val setInterestStateUseCase: SetInterestStateUseCase,
    private val userRepository: UserRepository,
) : ViewModel() {
    private val _getHomeDataState = MutableStateFlow<UiState<HomeModel>>(UiState.Empty)
    val getHomeDataState: StateFlow<UiState<HomeModel>> = _getHomeDataState

    private val _likeState = MutableStateFlow<Boolean?>(null)
    val likeState: StateFlow<Boolean?> = _likeState

    var likedPosition = -1
    var clickedPosition = -1

    private var currentPage = -1
    private var isPagingFinish = false
    private var totalPage = Int.MAX_VALUE

    init {
        getHomeDataFromServer()
    }

    fun getHomeDataFromServer() {
        if (isPagingFinish) return
        viewModelScope.launch {
            getHomeListByPageUseCase(++currentPage)
                .onSuccess {
                    totalPage =
                        ceil((it.pageInfo.totalElements.toDouble() / it.pageInfo.numberOfElements)).toInt() - 1
                    if (currentPage == totalPage) isPagingFinish = true
                    _getHomeDataState.value = UiState.Success(it)
                }
                .onFailure {
                    _getHomeDataState.value = UiState.Failure(it.message.toString())
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

    fun setDeviceToken(deviceToken: String) {
        userRepository.setDeviceToken(deviceToken)
    }
}
