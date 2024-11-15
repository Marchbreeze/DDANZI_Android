package co.orange.domain.usecase.auth

import co.orange.domain.entity.request.ReissueRequestModel
import co.orange.domain.repository.AuthRepository
import co.orange.domain.repository.UserRepository
import javax.inject.Inject

class AuthReissueTokenUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
) {
    suspend operator fun invoke() = runCatching {
        val response = authRepository.postReissueTokens(
            ReissueRequestModel(userRepository.getRefreshToken()),
        )
        userRepository.setTokens(
            response.accessToken,
            response.refreshToken,
        )
        return@runCatching response
    }
}