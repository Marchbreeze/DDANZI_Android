package co.orange.domain.usecase.buy

import co.orange.domain.entity.request.OrderRequestModel
import co.orange.domain.repository.BuyRepository
import javax.inject.Inject

class RequestBuyOrderUseCase @Inject constructor(
    private val buyRepository: BuyRepository
) {
    suspend operator fun invoke(orderId: String, optionList: List<Long>) =
        runCatching {
            val request = OrderRequestModel(
                orderId,
                optionList,
            )
            val response = buyRepository.postToRequestOrder(request)
            return@runCatching response
        }
}