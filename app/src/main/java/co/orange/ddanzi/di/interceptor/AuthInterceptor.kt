package co.orange.ddanzi.di.interceptor

import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import co.orange.auth.login.LoginActivity
import co.orange.core.amplitude.AmplitudeManager
import co.orange.core.extension.toast
import co.orange.domain.repository.UserRepository
import co.orange.domain.usecase.auth.ReissueTokenUseCase
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import timber.log.Timber
import javax.inject.Inject

class AuthInterceptor
@Inject
constructor(
    private val reissueTokenUseCase: ReissueTokenUseCase,
    private val userRepository: UserRepository,
    @ApplicationContext private val context: Context,
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        Timber.tag("okhttp").d("ACCESS TOKEN : ${userRepository.getAccessToken()}")

        val authRequest =
            if (userRepository.getAccessToken().isNotBlank()) {
                originalRequest.newBuilder().newAuthBuilder().build()
            } else {
                originalRequest
            }

        val response = chain.proceed(authRequest)

        if (response.code == CODE_TOKEN_EXPIRED) {
            try {
                runBlocking {
                    reissueTokenUseCase()
                }.onSuccess {
                    response.close()

                    val newRequest =
                        authRequest.newBuilder().removeHeader(AUTHORIZATION).newAuthBuilder()
                            .build()
                    return chain.proceed(newRequest)
                }
            } catch (t: Throwable) {
                Timber.tag("okhttp").d(t)
            }

            userRepository.clearInfo()

            Handler(Looper.getMainLooper()).post {
                AmplitudeManager.trackEvent("view_sign_up", mapOf("sign_up_from" to "token"))
                context.toast(TOKEN_EXPIRED_ERROR)
                Intent(context, LoginActivity::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    context.startActivity(this)
                }
            }
        }
        return response
    }

    private fun Request.Builder.newAuthBuilder() =
        this.addHeader(AUTHORIZATION, "$BEARER ${userRepository.getAccessToken()}")

    companion object {
        private const val CODE_TOKEN_EXPIRED = 401
        private const val TOKEN_EXPIRED_ERROR = "토큰이 만료되었어요\n다시 로그인 해주세요"
        private const val BEARER = "Bearer"
        private const val AUTHORIZATION = "Authorization"
    }
}
