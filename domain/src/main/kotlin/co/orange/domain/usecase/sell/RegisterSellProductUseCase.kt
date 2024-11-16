package co.orange.domain.usecase.sell

import co.orange.domain.entity.request.SellRegisterRequestModel
import co.orange.domain.repository.SellRepository
import javax.inject.Inject

class RegisterSellProductUseCase @Inject constructor(
    private val sellRepository: SellRepository
) {
    suspend operator fun invoke(
        productId: String,
        productName: String,
        dueDate: String?,
        uploadedUrl: String
    ) =
        runCatching {
            require(productId.isNotEmpty())
            requireNotNull(dueDate)
            val request = SellRegisterRequestModel(
                productId,
                productName,
                dueDate,
                uploadedUrl,
            )
            val response = sellRepository.postToRegisterProduct(request)
            return@runCatching response
        }
}