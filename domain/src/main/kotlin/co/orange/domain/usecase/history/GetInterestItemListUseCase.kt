package co.orange.domain.usecase.history

import co.orange.domain.repository.ProfileRepository
import javax.inject.Inject

class GetInterestItemListUseCase @Inject constructor(
    private val profileRepository: ProfileRepository,
) {
    suspend operator fun invoke() =
        runCatching {
            val response = profileRepository.getInterestHistory()
            return@runCatching response
        }
}