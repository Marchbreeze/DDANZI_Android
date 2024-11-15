package co.orange.domain.usecase.sell

import co.orange.domain.repository.SellRepository
import javax.inject.Inject

class GetBuyerInfoUseCase @Inject constructor(
    private val sellRepository: SellRepository
) {
    suspend operator fun invoke(orderId: String) =
        runCatching {
            val response = sellRepository.getBuyerInfo(orderId)
            return@runCatching response
        }
}