package co.orange.data.repositoryImpl

import co.orange.data.dataSource.InterestDataSource
import co.orange.domain.entity.response.InterestModel
import co.orange.domain.repository.InterestRepository
import javax.inject.Inject

class InterestRepositoryImpl
@Inject
constructor(
    private val interestDataSource: InterestDataSource,
) : InterestRepository {
    override suspend fun postInterest(productId: String): InterestModel =
        interestDataSource.postInterest(productId).data.toModel()

    override suspend fun deleteInterest(productId: String): InterestModel =
        interestDataSource.deleteInterest(productId).data.toModel()
}
