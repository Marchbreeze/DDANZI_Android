package co.orange.domain.usecase

import co.orange.domain.entity.request.SignUpRequestModel
import co.orange.domain.entity.response.IamportCertificationModel
import co.orange.domain.repository.AuthRepository
import co.orange.domain.repository.UserRepository
import javax.inject.Inject

class AuthSignUpAndSaveStatusUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
) {
    suspend operator fun invoke(
        response: IamportCertificationModel,
        isTermMarketingSelected: Boolean?
    ) = runCatching {
        val request = SignUpRequestModel(
            name = response.name.orEmpty(),
            phone = response.phone.orEmpty(),
            birth = response.birthday.orEmpty(),
            sex = response.gender?.uppercase().orEmpty(),
            isAgreedMarketingTerm = isTermMarketingSelected ?: false,
            ci = response.uniqueKey.orEmpty(),
        )
        val signUpModel = authRepository.postToSignUp(userRepository.getAccessToken(), request)
        userRepository.setUserStatus(signUpModel.status)
        return@runCatching signUpModel
    }
}