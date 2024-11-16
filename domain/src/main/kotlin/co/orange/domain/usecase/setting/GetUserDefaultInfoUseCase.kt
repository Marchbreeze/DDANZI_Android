package co.orange.domain.usecase.setting

import co.orange.domain.repository.SettingRepository
import co.orange.domain.repository.UserRepository
import javax.inject.Inject

class GetUserDefaultInfoUseCase @Inject constructor(
    private val settingRepository: SettingRepository,
    private val userRepository: UserRepository,
) {
    suspend operator fun invoke() =
        runCatching {
            val response = settingRepository.getSettingInfo()
            userRepository.setUserInfo(response.name, response.phone)
            return@runCatching response
        }
}