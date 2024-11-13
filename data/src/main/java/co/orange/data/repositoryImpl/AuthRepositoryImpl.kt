package co.orange.data.repositoryImpl

import co.orange.data.dataSource.AuthDataSource
import co.orange.data.dto.request.AuthRequestDto.Companion.toDto
import co.orange.data.dto.request.ReissueRequestDto.Companion.toDto
import co.orange.data.dto.request.SignUpRequestDto.Companion.toDto
import co.orange.domain.entity.request.AuthRequestModel
import co.orange.domain.entity.request.ReissueRequestModel
import co.orange.domain.entity.request.SignUpRequestModel
import co.orange.domain.entity.response.AuthTokenModel
import co.orange.domain.entity.response.ReissueTokenModel
import co.orange.domain.entity.response.SignUpModel
import co.orange.domain.repository.AuthRepository
import javax.inject.Inject

class AuthRepositoryImpl
@Inject
constructor(
    private val authDataSource: AuthDataSource,
) : AuthRepository {
    override suspend fun postReissueTokens(request: ReissueRequestModel): ReissueTokenModel =
        authDataSource.postReissueTokens(
            request.toDto(),
        ).data.toModel()

    override suspend fun postOauthDataToGetToken(request: AuthRequestModel): AuthTokenModel =
        authDataSource.postOauthDataToGetToken(request.toDto()).data.toModel()

    override suspend fun postToSignUp(
        accesstoken: String,
        request: SignUpRequestModel,
    ): SignUpModel =
        authDataSource.postToSignUp(
            "$BEARER $accesstoken",
            request.toDto(),
        ).data.toModel()

    override suspend fun getServerStatus(): Result<Boolean> =
        runCatching {
            authDataSource.getServerStatus().data
        }

    companion object {
        private const val BEARER = "Bearer"
    }
}
