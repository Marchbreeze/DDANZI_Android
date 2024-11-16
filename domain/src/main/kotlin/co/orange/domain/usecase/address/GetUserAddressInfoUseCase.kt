package co.orange.domain.usecase.address

import co.orange.domain.repository.SettingRepository
import javax.inject.Inject

class GetUserAddressInfoUseCase @Inject constructor(
    private val settingRepository: SettingRepository,
) {
    suspend operator fun invoke() =
        runCatching {
            val response = settingRepository.getUserAddress()
            return@runCatching response
        }
}