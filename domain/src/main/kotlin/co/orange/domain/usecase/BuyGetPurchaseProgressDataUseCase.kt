package co.orange.domain.usecase

import co.orange.domain.repository.BuyRepository
import javax.inject.Inject

class BuyGetPurchaseProgressDataUseCase @Inject constructor(
    private val buyRepository: BuyRepository
) {
    suspend operator fun invoke(productId: String) = runCatching {
        val response = buyRepository.getBuyProgressData(productId)
        return@runCatching response
    }
}