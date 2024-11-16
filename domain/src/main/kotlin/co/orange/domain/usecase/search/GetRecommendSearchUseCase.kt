package co.orange.domain.usecase.search

import co.orange.domain.repository.DeviceRepository
import javax.inject.Inject

class GetRecommendSearchUseCase @Inject constructor(
    private val deviceRepository: DeviceRepository
) {
    suspend operator fun invoke() =
        runCatching {
            val response = deviceRepository.getSearchInfo()
            return@runCatching response
        }
}