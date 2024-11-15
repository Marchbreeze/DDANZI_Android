package co.orange.domain.usecase.buy

import co.orange.domain.repository.BuyRepository
import javax.inject.Inject

class GetBuyOrderInfoUseCase @Inject constructor(
    private val buyRepository: BuyRepository
) {
    suspend operator fun invoke(orderId: String) =
        runCatching {
            val response = buyRepository.getOrderInfo(orderId)
            return@runCatching response
        }
}