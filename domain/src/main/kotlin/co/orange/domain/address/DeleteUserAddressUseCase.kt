package co.orange.domain.address

import co.orange.domain.repository.SettingRepository
import javax.inject.Inject

class DeleteUserAddressUseCase @Inject constructor(
    private val settingRepository: SettingRepository,
) {
    suspend operator fun invoke(addressId: Long) =
        runCatching {
            val response = settingRepository.deleteUserAddress(addressId)
            return@runCatching response
        }
}