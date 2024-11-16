package co.orange.domain.usecase.alarm

import co.orange.domain.repository.HomeRepository
import javax.inject.Inject

class GetAlarmListUseCase @Inject constructor(
    private val homeRepository: HomeRepository
) {
    suspend operator fun invoke() =
        runCatching {
            val response = homeRepository.getAlarmList()
            return@runCatching response
        }
}