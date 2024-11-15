package co.orange.domain.usecase.upload

import co.orange.domain.repository.UploadRepository
import javax.inject.Inject

class UploadPutImageToCloudUseCase @Inject constructor(
    private val uploadRepository: UploadRepository
) {
    suspend operator fun invoke(url: String, selectedImageUri: String) =
        runCatching {
            val response = uploadRepository.putImageToCloud(url, selectedImageUri)
            return@runCatching response
        }
}