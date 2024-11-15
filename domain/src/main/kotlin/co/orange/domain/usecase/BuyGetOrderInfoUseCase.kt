package co.orange.domain.usecase

import co.orange.domain.repository.BuyRepository
import javax.inject.Inject

class BuyGetOrderInfoUseCase @Inject constructor(
    private val buyRepository: BuyRepository
) {
    suspend operator fun invoke(orderId: String) =
        runCatching {
            val response = buyRepository.getOrderInfo(orderId)
            return@runCatching response
        }
}