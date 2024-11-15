package co.orange.domain.usecase.sell

import co.orange.domain.enums.OrderStatus
import co.orange.domain.repository.SellRepository
import javax.inject.Inject

class ConfirmSellOrderUseCase @Inject constructor(
    private val sellRepository: SellRepository
) {
    suspend operator fun invoke(orderId: String) =
        runCatching {
            val response = sellRepository.patchOrderConfirm(orderId)
            if (response.orderStatus == OrderStatus.SHIPPING.name) {
                return@runCatching response
            } else {
                throw IllegalArgumentException()
            }
        }
}