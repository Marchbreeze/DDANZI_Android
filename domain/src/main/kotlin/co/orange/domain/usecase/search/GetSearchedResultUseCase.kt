package co.orange.domain.usecase.search

import co.orange.domain.repository.HomeRepository
import javax.inject.Inject

class GetSearchedResultUseCase @Inject constructor(
    private val homeRepository: HomeRepository
) {
    suspend operator fun invoke(keyword: String) =
        runCatching {
            val response = homeRepository.getSearchResult(keyword)
            return@runCatching response
        }
}