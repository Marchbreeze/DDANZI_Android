package co.orange.domain.usecase.home

import co.orange.domain.repository.HomeRepository
import javax.inject.Inject

class GetHomeListByPageUseCase @Inject constructor(
    private val homeRepository: HomeRepository
) {
    suspend operator fun invoke(currentPage: Int) =
        runCatching {
            val response = homeRepository.getHomeData(currentPage)
            return@runCatching response
        }
}