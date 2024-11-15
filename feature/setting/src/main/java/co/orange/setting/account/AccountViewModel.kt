package co.orange.setting.account

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.orange.core.state.UiState
import co.orange.domain.repository.SettingRepository
import co.orange.domain.repository.UserRepository
import co.orange.domain.usecase.setting.LogoutAccountUseCase
import com.kakao.sdk.user.UserApiClient
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AccountViewModel
    @Inject
    constructor(
        private val logoutAccountUseCase: LogoutAccountUseCase,
    ) : ViewModel() {

        private val _userLogoutState = MutableStateFlow<UiState<Boolean>>(UiState.Empty)
        val userLogoutState: StateFlow<UiState<Boolean>> = _userLogoutState

        private val _userQuitState = MutableStateFlow<UiState<String>>(UiState.Empty)
        val userQuitState: StateFlow<UiState<String>> = _userQuitState

        fun logoutKakaoAccount() {
            _userLogoutState.value = UiState.Loading
            UserApiClient.instance.logout { error ->
                viewModelScope.launch {
                    if (error == null) {
                        logoutFromServer()
                    } else {
                        _userLogoutState.value = UiState.Failure(error.message.toString())
                    }
                }
            }
        }

        private fun logoutFromServer() {
            viewModelScope.launch {
                logoutAccountUseCase()
                    .onSuccess {
                        _userLogoutState.value = UiState.Success(it)
                    }.onFailure {
                        _userLogoutState.value = UiState.Failure(it.message.toString())
                    }
            }
        }

        fun quitKakaoAccount() {
            _userQuitState.value = UiState.Loading
            UserApiClient.instance.unlink { error ->
                viewModelScope.launch {
                    if (error == null) {
                        quitFromServer()
                    } else {
                        _userQuitState.value = UiState.Failure(error.message.toString())
                    }
                }
            }
        }

        private fun quitFromServer() {
            viewModelScope.launch {
                settingRepository.deleteToUserQuit()
                    .onSuccess {
                        userRepository.clearInfo()
                        _userQuitState.value = UiState.Success(it.nickname)
                    }.onFailure {
                        _userQuitState.value = UiState.Failure(it.message.toString())
                    }
            }
        }
    }
