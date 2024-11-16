package co.orange.data.repositoryImpl

import co.orange.data.dataSource.ProfileDataSource
import co.orange.domain.entity.response.HistoryBuyModel
import co.orange.domain.entity.response.HistoryInterestModel
import co.orange.domain.entity.response.HistorySellModel
import co.orange.domain.entity.response.NicknameModel
import co.orange.domain.repository.ProfileRepository
import javax.inject.Inject

class ProfileRepositoryImpl
@Inject
constructor(
    private val profileDataSource: ProfileDataSource,
) : ProfileRepository {
    override suspend fun getNickname(): NicknameModel =
        profileDataSource.getNickname().data.toModel()

    override suspend fun getInterestHistory(): HistoryInterestModel =
        profileDataSource.getInterestHistory().data.toModel()

    override suspend fun getBuyHistory(): HistoryBuyModel =
        profileDataSource.getBuyHistory().data.toModel()

    override suspend fun getSellHistory(): HistorySellModel =
        profileDataSource.getSellHistory().data.toModel()
}
