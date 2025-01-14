package co.orange.data.repositoryImpl

import co.orange.data.BuildConfig.IAMPORT_API_KEY
import co.orange.data.BuildConfig.IAMPORT_API_SECRET
import co.orange.data.dataSource.IamportDataSource
import co.orange.data.dto.request.IamportTokenRequestDto
import co.orange.domain.entity.response.IamportCertificationModel
import co.orange.domain.entity.response.IamportTokenModel
import co.orange.domain.repository.IamportRepository
import javax.inject.Inject

class IamportRepositoryImpl
@Inject
constructor(
    private val iamportDataSource: IamportDataSource,
) : IamportRepository {
    override suspend fun postToGetIamportToken(): IamportTokenModel? =
        iamportDataSource.postToGetIamportToken(
            IamportTokenRequestDto(
                IAMPORT_API_KEY,
                IAMPORT_API_SECRET,
            ),
        ).response?.toModel()

    override suspend fun getIamportCertificationData(
        accessToken: String,
        impUid: String,
    ): IamportCertificationModel? =
        iamportDataSource.getIamportCertificationData(
            "$BEARER $accessToken",
            impUid,
        ).response?.toModel()

    companion object {
        private const val BEARER = "Bearer"
    }
}
