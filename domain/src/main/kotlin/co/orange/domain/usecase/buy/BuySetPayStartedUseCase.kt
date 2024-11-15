package co.orange.domain.usecase.buy

import co.orange.domain.entity.request.PayStartRequestModel
import co.orange.domain.entity.response.BuyProgressModel
import co.orange.domain.repository.BuyRepository
import javax.inject.Inject

class BuySetPayStartedUseCase @Inject constructor(
    private val buyRepository: BuyRepository
) {
    suspend operator fun invoke(buyProgressData: BuyProgressModel?, payMethod: String) =
        runCatching {
            requireNotNull(buyProgressData)
            val request = PayStartRequestModel(
                buyProgressData.productId,
                buyProgressData.charge,
                buyProgressData.totalPrice,
                payMethod,
            )
            val response = buyRepository.postPaymentStart(request)
            if (response.payStatus == PAY_STATUS_PENDING) {
                return@runCatching response
            } else {
                throw IllegalArgumentException()
            }
        }

    companion object {
        private const val PAY_STATUS_PENDING = "PENDING"
    }
}