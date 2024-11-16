package co.orange.domain.usecase.setting

import co.orange.domain.repository.SettingRepository
import co.orange.domain.repository.UserRepository
import javax.inject.Inject

class LogoutAccountUseCase @Inject constructor(
    private val settingRepository: SettingRepository,
    private val userRepository: UserRepository,
) {
    suspend operator fun invoke() =
        runCatching {
            val response = settingRepository.postUserLogout()
            userRepository.clearInfo()
            return@runCatching response
        }
}