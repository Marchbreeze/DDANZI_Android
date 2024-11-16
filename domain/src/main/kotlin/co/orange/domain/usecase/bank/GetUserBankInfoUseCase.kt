package co.orange.domain.usecase.bank

import co.orange.domain.repository.SettingRepository
import javax.inject.Inject

class GetUserBankInfoUseCase @Inject constructor(
    private val settingRepository: SettingRepository,
) {
    suspend operator fun invoke() =
        runCatching {
            val response = settingRepository.getUserBank()
            return@runCatching response
        }
}