package co.orange.domain.usecase.network

import co.orange.domain.repository.AuthRepository
import javax.inject.Inject

class GetServerAvailableStatusUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke() =
        runCatching {
            val response = authRepository.getServerStatus()
            if (response) {
                return@runCatching true
            } else {
                throw Exception()
            }
        }
}