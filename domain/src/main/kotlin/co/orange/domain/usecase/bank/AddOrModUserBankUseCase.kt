package co.orange.domain.usecase.bank

import co.orange.domain.entity.request.BankRequestModel
import co.orange.domain.repository.SettingRepository
import javax.inject.Inject

class AddOrModUserBankUseCase @Inject constructor(
    private val settingRepository: SettingRepository,
) {
    suspend operator fun invoke(
        accountId: Long,
        ownerName: String,
        bankCode: String,
        accountNumber: String
    ) =
        runCatching {
            val request = BankRequestModel(
                ownerName,
                bankCode,
                accountNumber,
            )
            val response = if (accountId == DEFAULT_ID) {
                settingRepository.postToAddBank(request)
            } else {
                settingRepository.putToModBank(accountId, request)
            }
            return@runCatching response
        }

    companion object {
        const val DEFAULT_ID: Long = -1
    }
}