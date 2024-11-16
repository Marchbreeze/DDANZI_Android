package co.orange.data.repositoryImpl

import co.orange.data.dataSource.SellDataSource
import co.orange.data.dto.request.SellCheckRequestDto.Companion.toDto
import co.orange.data.dto.request.SellRegisterRequestDto.Companion.toDto
import co.orange.domain.entity.request.SellCheckRequestModel
import co.orange.domain.entity.request.SellRegisterRequestModel
import co.orange.domain.entity.response.OrderConfirmModel
import co.orange.domain.entity.response.SellBuyerInfoModel
import co.orange.domain.entity.response.SellCheckedProductModel
import co.orange.domain.entity.response.SellInfoModel
import co.orange.domain.entity.response.SellProductModel
import co.orange.domain.entity.response.SellRegisteredModel
import co.orange.domain.entity.response.SignedUrlModel
import co.orange.domain.repository.SellRepository
import javax.inject.Inject

class SellRepositoryImpl @Inject constructor(
    private val sellDataSource: SellDataSource,
) : SellRepository {
    override suspend fun getSignedUrl(fileName: String): SignedUrlModel =
        sellDataSource.getSignedUrl(fileName).data.toModel()

    override suspend fun postToCheckProduct(request: SellCheckRequestModel): SellCheckedProductModel =
        sellDataSource.postToCheckProduct(request.toDto()).data.toModel()

    override suspend fun getProductToSell(productId: String): SellProductModel =
        sellDataSource.getProductToSell(productId).data.toModel()

    override suspend fun postToRegisterProduct(request: SellRegisterRequestModel): SellRegisteredModel =
        sellDataSource.postToRegisterProduct(request.toDto()).data.toModel()

    override suspend fun getItemDetailInfo(itemId: String): SellInfoModel =
        sellDataSource.getItemDetailInfo(itemId).data.toModel()

    override suspend fun getBuyerInfo(orderId: String): SellBuyerInfoModel =
        sellDataSource.getBuyerInfo(orderId).data.toModel()

    override suspend fun patchOrderConfirm(orderId: String): OrderConfirmModel =
        sellDataSource.patchOrderConfirm(orderId).data.toModel()

    override suspend fun deleteSellingItem(itemId: String): Boolean =
        sellDataSource.deleteSellingItem(itemId).data
}