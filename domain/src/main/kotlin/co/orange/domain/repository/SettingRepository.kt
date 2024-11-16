package co.orange.domain.repository

import co.orange.domain.entity.request.AddressRequestModel
import co.orange.domain.entity.request.BankRequestModel
import co.orange.domain.entity.response.AddressModel
import co.orange.domain.entity.response.BankModel
import co.orange.domain.entity.response.NicknameModel
import co.orange.domain.entity.response.SettingInfoModel

interface SettingRepository {
    suspend fun getSettingInfo(): SettingInfoModel

    suspend fun postToAddAddress(request: AddressRequestModel): AddressModel

    suspend fun putToModAddress(
        addressId: Long,
        request: AddressRequestModel,
    ): AddressModel

    suspend fun getUserAddress(): AddressModel

    suspend fun deleteUserAddress(addressId: Long): Boolean

    suspend fun postUserLogout(): Boolean

    suspend fun deleteToUserQuit(): NicknameModel

    suspend fun postToAddBank(request: BankRequestModel): BankModel

    suspend fun putToModBank(
        accountId: Long,
        request: BankRequestModel,
    ): BankModel

    suspend fun getUserBank(): BankModel
}
