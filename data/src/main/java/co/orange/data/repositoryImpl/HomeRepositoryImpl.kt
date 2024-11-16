package co.orange.data.repositoryImpl

import co.orange.data.dataSource.HomeDataSource
import co.orange.domain.entity.response.AlarmListModel
import co.orange.domain.entity.response.HomeModel
import co.orange.domain.entity.response.SearchResultModel
import co.orange.domain.repository.HomeRepository
import javax.inject.Inject

class HomeRepositoryImpl
@Inject
constructor(
    private val homeDataSource: HomeDataSource,
) : HomeRepository {
    override suspend fun getHomeData(page: Int): HomeModel =
        homeDataSource.getHomeData(page).data.toModel()

    override suspend fun getSearchResult(keyword: String): SearchResultModel =
        homeDataSource.getSearchResult(keyword).data.toModel()

    override suspend fun getAlarmList(): AlarmListModel =
        homeDataSource.getAlarmList().data.toModel()

    override suspend fun patchToReadAlarm(alarmId: Long): Boolean =
        homeDataSource.patchToReadAlarm(alarmId).data
}
