package co.orange.domain.usecase.buy

import co.orange.domain.entity.request.PayEndRequestModel
import co.orange.domain.repository.BuyRepository
import javax.inject.Inject

class BuySetPayFinishedUseCase @Inject constructor(
    private val buyRepository: BuyRepository
) {
    suspend operator fun invoke(orderId: String, isSuccess: Boolean?) =
        runCatching {
            val request = PayEndRequestModel(
                orderId,
                if (isSuccess != false) PAY_SUCCESS else PAY_FAILURE,
            )
            val response = buyRepository.patchPaymentEnd(request)
            return@runCatching response
        }

    companion object {
        const val PAY_SUCCESS = "PAID"
        const val PAY_FAILURE = "FAILED"
    }
}