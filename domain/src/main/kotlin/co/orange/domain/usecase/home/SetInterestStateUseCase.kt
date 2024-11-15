package co.orange.domain.usecase.home

import co.orange.domain.repository.InterestRepository
import javax.inject.Inject

class SetInterestStateUseCase @Inject constructor(
    private val interestRepository: InterestRepository
) {
    suspend operator fun invoke(likeState: Boolean, productId: String) =
        runCatching {
            if (likeState) {
                interestRepository.deleteInterest(productId)
                return@runCatching false
            } else {
                interestRepository.postInterest(productId)
                return@runCatching true
            }
        }
}