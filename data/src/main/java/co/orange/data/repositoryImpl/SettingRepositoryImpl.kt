package co.orange.data.repositoryImpl

import co.orange.data.dataSource.SettingDataSource
import co.orange.data.dto.request.AddressRequestDto.Companion.toDto
import co.orange.data.dto.request.BankRequestDto.Companion.toDto
import co.orange.domain.entity.request.AddressRequestModel
import co.orange.domain.entity.request.BankRequestModel
import co.orange.domain.entity.response.AddressModel
import co.orange.domain.entity.response.BankModel
import co.orange.domain.entity.response.NicknameModel
import co.orange.domain.entity.response.SettingInfoModel
import co.orange.domain.repository.SettingRepository
import javax.inject.Inject

class SettingRepositoryImpl @Inject constructor(
    private val settingDataSource: SettingDataSource,
) : SettingRepository {

    override suspend fun getSettingInfo(): SettingInfoModel =
        settingDataSource.getSettingInfo().data.toModel()

    override suspend fun postToAddAddress(request: AddressRequestModel): AddressModel =
        settingDataSource.postToAddAddress(request.toDto()).data.toModel()

    override suspend fun putToModAddress(
        addressId: Long,
        request: AddressRequestModel
    ): AddressModel =
        settingDataSource.putToModAddress(addressId, request.toDto()).data.toModel()

    override suspend fun getUserAddress(): AddressModel =
        settingDataSource.getUserAddress().data.toModel()

    override suspend fun deleteUserAddress(addressId: Long): Boolean =
        settingDataSource.deleteUserAddress(addressId).data

    override suspend fun postUserLogout(): Boolean =
        settingDataSource.postUserLogout().data

    override suspend fun deleteToUserQuit(): NicknameModel =
        settingDataSource.deleteToUserQuit().data.toModel()

    override suspend fun postToAddBank(request: BankRequestModel): BankModel =
        settingDataSource.postToAddBank(request.toDto()).data.toModel()

    override suspend fun putToModBank(accountId: Long, request: BankRequestModel): BankModel =
        settingDataSource.putToModBank(accountId, request.toDto()).data.toModel()

    override suspend fun getUserBank(): BankModel =
        settingDataSource.getUserBank().data.toModel()
}