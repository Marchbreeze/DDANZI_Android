package co.orange.domain.usecase.history

import co.orange.domain.repository.ProfileRepository
import javax.inject.Inject

class GetSellItemListUseCase @Inject constructor(
    private val profileRepository: ProfileRepository,
) {
    suspend operator fun invoke() =
        runCatching {
            val response = profileRepository.getSellHistory()
            return@runCatching response
        }
}