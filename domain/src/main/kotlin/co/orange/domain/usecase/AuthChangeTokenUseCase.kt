package co.orange.domain.usecase

import co.orange.domain.entity.request.AuthRequestModel
import co.orange.domain.repository.AuthRepository
import co.orange.domain.repository.UserRepository

class AuthChangeTokenUseCase(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(accessToken: String, fcmToken: String) = runCatching {
        val authTokenModel = authRepository.postOauthDataToGetToken(
            AuthRequestModel(
                accessToken,
                KAKAO,
                userRepository.getDeviceToken(),
                ANDROID,
                fcmToken,
            ),
        )
        userRepository.setTokens(authTokenModel.accesstoken, authTokenModel.refreshtoken)
        userRepository.setUserStatus(authTokenModel.status)
        return@runCatching authTokenModel.status
    }

    companion object {
        const val KAKAO = "KAKAO"
        const val ANDROID = "ANDROID"
    }
}