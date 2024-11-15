package co.orange.domain.usecase.certificate

import co.orange.domain.entity.response.IamportCertificationModel
import co.orange.domain.repository.IamportRepository
import co.orange.domain.repository.UserRepository
import javax.inject.Inject

class GetPhoneCertificateDataUseCase @Inject constructor(
    private val iamportRepository: IamportRepository,
    private val userRepository: UserRepository,
) {
    suspend operator fun invoke(accessToken: String, certificatedUid: String) = runCatching {
        val response: IamportCertificationModel? =
            iamportRepository.getIamportCertificationData(accessToken, certificatedUid)
        if (response != null) {
            userRepository.setUserInfo(
                userName = response.name.orEmpty(),
                userPhone = response.phone?.toPhoneFrom().orEmpty(),
            )
            return@runCatching response
        } else {
            throw IllegalArgumentException()
        }
    }

    private fun String.toPhoneFrom(): String? =
        if (this.length == 11) {
            "${this.substring(0, 3)}-${this.substring(3, 7)}-${this.substring(7)}"
        } else {
            null
        }
}