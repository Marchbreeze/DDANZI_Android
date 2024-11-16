package co.orange.domain.repository

import co.orange.domain.entity.response.AlarmListModel
import co.orange.domain.entity.response.HomeModel
import co.orange.domain.entity.response.SearchResultModel

interface HomeRepository {
    suspend fun getHomeData(page: Int): HomeModel

    suspend fun getSearchResult(keyword: String): SearchResultModel

    suspend fun getAlarmList(): AlarmListModel

    suspend fun patchToReadAlarm(alarmId: Long): Boolean
}
