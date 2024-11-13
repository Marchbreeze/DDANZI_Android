package co.orange.domain.usecase

import co.orange.domain.entity.response.IamportTokenModel
import co.orange.domain.repository.IamportRepository
import javax.inject.Inject

class IamportGetTokenUseCase @Inject constructor(
    private val iamportRepository: IamportRepository,
) {
    suspend operator fun invoke(certificatedUid: String) = runCatching {
        val response: IamportTokenModel? = iamportRepository.postToGetIamportToken()
        if (certificatedUid.isNotBlank() && response != null) {
            return@runCatching response.accessToken
        } else {
            throw IllegalArgumentException()
        }
    }
}