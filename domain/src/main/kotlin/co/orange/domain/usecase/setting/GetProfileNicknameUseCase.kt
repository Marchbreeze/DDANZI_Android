package co.orange.domain.usecase.setting

import co.orange.domain.repository.ProfileRepository
import javax.inject.Inject

class GetProfileNicknameUseCase @Inject constructor(
    private val profileRepository: ProfileRepository
) {
    suspend operator fun invoke() =
        runCatching {
            val response = profileRepository.getNickname()
            return@runCatching response
        }
}