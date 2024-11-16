package co.orange.domain.address

import co.orange.domain.entity.request.AddressRequestModel
import co.orange.domain.repository.SettingRepository
import javax.inject.Inject

class AddOrModUserAddressUseCase @Inject constructor(
    private val settingRepository: SettingRepository,
) {
    suspend operator fun invoke(
        addressId: Long,
        recipient: String,
        zipCode: String,
        address: String,
        detailAddress: String,
        recipientPhone: String,
    ) =
        runCatching {
            val request = AddressRequestModel(
                recipient,
                zipCode,
                TYPE_ROAD,
                address,
                detailAddress,
                recipientPhone,
            )
            val response = if (addressId == DEFAULT_ID) {
                settingRepository.postToAddAddress(request)
            } else {
                settingRepository.putToModAddress(addressId, request)
            }
            return@runCatching response
        }

    companion object {
        private const val DEFAULT_ID: Long = -1
        private const val TYPE_ROAD = "ROAD"
    }
}