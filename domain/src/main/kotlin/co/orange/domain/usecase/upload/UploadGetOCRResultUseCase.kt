package co.orange.domain.usecase.upload

import co.orange.domain.entity.request.SellCheckRequestModel
import co.orange.domain.repository.SellRepository
import javax.inject.Inject

class UploadGetOCRResultUseCase @Inject constructor(
    private val sellRepository: SellRepository
) {
    suspend operator fun invoke(uploadedUrl: String) =
        runCatching {
            val response = sellRepository.postToCheckProduct(SellCheckRequestModel(uploadedUrl))
            if (response.productId.isNotEmpty()) {
                return@runCatching response
            } else {
                throw IllegalArgumentException()
            }
        }
}