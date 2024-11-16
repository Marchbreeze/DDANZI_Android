package co.orange.domain.repository

import co.orange.domain.entity.response.HistoryBuyModel
import co.orange.domain.entity.response.HistoryInterestModel
import co.orange.domain.entity.response.HistorySellModel
import co.orange.domain.entity.response.NicknameModel

interface ProfileRepository {
    suspend fun getNickname(): NicknameModel

    suspend fun getInterestHistory(): HistoryInterestModel

    suspend fun getBuyHistory(): HistoryBuyModel

    suspend fun getSellHistory(): HistorySellModel
}
