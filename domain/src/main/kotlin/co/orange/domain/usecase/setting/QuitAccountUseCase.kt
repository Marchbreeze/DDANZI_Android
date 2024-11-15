package co.orange.domain.usecase.setting

import co.orange.domain.repository.SettingRepository
import co.orange.domain.repository.UserRepository
import javax.inject.Inject

class QuitAccountUseCase @Inject constructor(
    private val settingRepository: SettingRepository,
    private val userRepository: UserRepository,
) {
    suspend operator fun invoke() =
        runCatching {
            val response = settingRepository.deleteToUserQuit()
            userRepository.clearInfo()
            return@runCatching response
        }
}