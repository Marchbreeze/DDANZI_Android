package co.orange.domain.usecase.sell

import co.orange.domain.repository.SellRepository
import javax.inject.Inject

class GetSellProductInfoUseCase @Inject constructor(
    private val sellRepository: SellRepository
) {
    suspend operator fun invoke(productId: String) =
        runCatching {
            val response = sellRepository.getProductToSell(productId)
            return@runCatching response
        }
}