package co.orange.domain.usecase

import co.orange.domain.entity.request.AuthRequestModel
import co.orange.domain.repository.AuthRepository
import co.orange.domain.repository.UserRepository
import javax.inject.Inject

class AuthChangeTokenAndSaveUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(accessToken: String, fcmToken: String) = runCatching {
        val response = authRepository.postOauthDataToGetToken(
            AuthRequestModel(
                accessToken,
                KAKAO,
                userRepository.getDeviceToken(),
                ANDROID,
                fcmToken,
            ),
        )
        userRepository.setTokens(response.accesstoken, response.refreshtoken)
        userRepository.setUserStatus(response.status)
        return@runCatching response.status
    }

    companion object {
        const val KAKAO = "KAKAO"
        const val ANDROID = "ANDROID"
    }
}