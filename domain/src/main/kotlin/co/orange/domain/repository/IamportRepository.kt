package co.orange.domain.repository

import co.orange.domain.entity.response.IamportCertificationModel
import co.orange.domain.entity.response.IamportTokenModel

interface IamportRepository {
    suspend fun postToGetIamportToken(): IamportTokenModel?

    suspend fun getIamportCertificationData(
        accessToken: String,
        impUid: String,
    ): IamportCertificationModel?
}
