package co.orange.domain.usecase.auth

import co.orange.domain.entity.request.SignUpRequestModel
import co.orange.domain.entity.response.IamportCertificationModel
import co.orange.domain.repository.AuthRepository
import co.orange.domain.repository.UserRepository
import javax.inject.Inject

class SignUpAndSaveStatusUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
) {
    suspend operator fun invoke(
        iamportCertificationModel: IamportCertificationModel,
        isTermMarketingSelected: Boolean?
    ) = runCatching {
        val request = SignUpRequestModel(
            name = iamportCertificationModel.name.orEmpty(),
            phone = iamportCertificationModel.phone.orEmpty(),
            birth = iamportCertificationModel.birthday.orEmpty(),
            sex = iamportCertificationModel.gender?.uppercase().orEmpty(),
            isAgreedMarketingTerm = isTermMarketingSelected ?: false,
            ci = iamportCertificationModel.uniqueKey.orEmpty(),
        )
        val response = authRepository.postToSignUp(
            "$BEARER ${userRepository.getAccessToken()}", request
        )
        userRepository.setUserStatus(response.status)
        return@runCatching response
    }

    companion object {
        private const val BEARER = "Bearer"
    }
}