package co.orange.auth.login

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.orange.core.state.UiState
import co.orange.domain.usecase.AuthChangeTokenAndSaveUseCase
import com.google.firebase.messaging.FirebaseMessaging
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel
    @Inject
    constructor(
        private val authChangeTokenAndSaveUseCase: AuthChangeTokenAndSaveUseCase,
    ) : ViewModel() {
        private val _isAppLoginAvailable = MutableStateFlow(true)
        val isAppLoginAvailable: StateFlow<Boolean> = _isAppLoginAvailable

        private val _changeTokenState = MutableStateFlow<UiState<String>>(UiState.Empty)
        val changeTokenState: StateFlow<UiState<String>> = _changeTokenState

        private val _getFCMTokenResult = MutableSharedFlow<Boolean>()
        val getFCMTokenResult: SharedFlow<Boolean> = _getFCMTokenResult

        private var appLoginCallback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
            if (error != null) {
                if (!(error is ClientError && error.reason == ClientErrorCause.Cancelled)) {
                    _isAppLoginAvailable.value = false
                }
            } else if (token != null) {
                getFCMToken(token.accessToken)
            }
        }

        private var webLoginCallback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
            if (error == null && token != null) {
                getFCMToken(token.accessToken)
            }
        }

        fun startLogInWithKakao(context: Context) {
            if (UserApiClient.instance.isKakaoTalkLoginAvailable(context) && isAppLoginAvailable.value) {
                UserApiClient.instance.loginWithKakaoTalk(
                    context = context,
                    callback = appLoginCallback,
                )
            } else {
                UserApiClient.instance.loginWithKakaoAccount(
                    context = context,
                    callback = webLoginCallback,
                )
            }
        }

        private fun getFCMToken(accessToken: String) {
            FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
                viewModelScope.launch {
                    if (task.isSuccessful) {
                        changeTokenFromServer(accessToken, task.result)
                    } else {
                        _getFCMTokenResult.emit(false)
                    }
                }
            }
        }

        private fun changeTokenFromServer(
            accessToken: String,
            fcmToken: String,
        ) {
            viewModelScope.launch {
                authChangeTokenAndSaveUseCase(accessToken, fcmToken)
                    .onSuccess {
                        _changeTokenState.value = UiState.Success(it)
                    }
                    .onFailure {
                        _changeTokenState.value = UiState.Failure(it.message.orEmpty())
                    }
            }
        }
    }
