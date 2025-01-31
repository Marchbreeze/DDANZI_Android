package co.orange.domain.usecase.buy

import co.orange.domain.repository.BuyRepository
import javax.inject.Inject

class GetBuyProgressInfoUseCase @Inject constructor(
    private val buyRepository: BuyRepository
) {
    suspend operator fun invoke(productId: String) = runCatching {
        val response = buyRepository.getBuyProgressData(productId)
        return@runCatching response
    }
}