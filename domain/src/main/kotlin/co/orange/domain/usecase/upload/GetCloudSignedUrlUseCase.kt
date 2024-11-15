package co.orange.domain.usecase.upload

import co.orange.domain.repository.SellRepository
import javax.inject.Inject

class GetCloudSignedUrlUseCase @Inject constructor(
    private val sellRepository: SellRepository
) {
    suspend operator fun invoke(selectedImageName: String) =
        runCatching {
            val response = sellRepository.getSignedUrl(selectedImageName)
            return@runCatching response
        }
}