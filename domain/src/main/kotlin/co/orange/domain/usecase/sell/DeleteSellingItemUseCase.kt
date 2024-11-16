package co.orange.domain.usecase.sell

import co.orange.domain.repository.SellRepository
import javax.inject.Inject

class DeleteSellingItemUseCase @Inject constructor(
    private val sellRepository: SellRepository
) {
    suspend operator fun invoke(itemId: String) =
        runCatching {
            val response = sellRepository.deleteSellingItem(itemId)
            return@runCatching response
        }
}