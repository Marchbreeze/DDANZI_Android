package co.orange.domain.usecase

import co.orange.domain.enums.OrderStatus
import co.orange.domain.repository.BuyRepository
import javax.inject.Inject

class BuyConfirmOrderUseCase @Inject constructor(
    private val buyRepository: BuyRepository
) {
    suspend operator fun invoke(orderId: String) =
        runCatching {
            val response = buyRepository.patchOrderConfirm(orderId)
            if (response.orderStatus == OrderStatus.COMPLETED.name) {
                return@runCatching response
            } else {
                throw IllegalArgumentException()
            }
        }
}