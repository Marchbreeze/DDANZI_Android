package co.orange.domain.usecase.alarm

import co.orange.domain.repository.HomeRepository
import javax.inject.Inject

class PatchAlarmToReadUseCase @Inject constructor(
    private val homeRepository: HomeRepository
) {
    suspend operator fun invoke(alarmId: Long) =
        runCatching {
            val response = homeRepository.patchToReadAlarm(alarmId)
            return@runCatching response
        }
}