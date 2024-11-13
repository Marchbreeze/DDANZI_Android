package co.orange.domain.usecase

import co.orange.domain.entity.response.IamportCertificationModel
import co.orange.domain.repository.IamportRepository
import javax.inject.Inject

class IamportGetDataUseCase @Inject constructor(
    private val iamportRepository: IamportRepository,
) {
    suspend operator fun invoke(accessToken: String, certificatedUid: String) = runCatching {
        val authCertificationModel: IamportCertificationModel? =
            iamportRepository.getIamportCertificationData(accessToken, certificatedUid)
        if (authCertificationModel != null) {
            return@runCatching authCertificationModel
        } else {
            throw IllegalArgumentException()
        }
    }
}