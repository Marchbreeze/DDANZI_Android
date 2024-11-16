package co.orange.domain.usecase.home

import co.orange.domain.repository.DeviceRepository
import javax.inject.Inject

class GetProductDetailUseCase @Inject constructor(
    private val deviceRepository: DeviceRepository
) {
    suspend operator fun invoke(productId: String) =
        runCatching {
            val response = deviceRepository.getProductDetail(productId)
            return@runCatching response
        }
}