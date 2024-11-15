package co.orange.domain.repository

import co.orange.domain.entity.request.SellCheckRequestModel
import co.orange.domain.entity.request.SellRegisterRequestModel
import co.orange.domain.entity.response.OrderConfirmModel
import co.orange.domain.entity.response.SellBuyerInfoModel
import co.orange.domain.entity.response.SellCheckedProductModel
import co.orange.domain.entity.response.SellInfoModel
import co.orange.domain.entity.response.SellProductModel
import co.orange.domain.entity.response.SellRegisteredModel
import co.orange.domain.entity.response.SignedUrlModel

interface SellRepository {
    suspend fun getSignedUrl(fileName: String): SignedUrlModel

    suspend fun postToCheckProduct(request: SellCheckRequestModel): SellCheckedProductModel

    suspend fun getProductToSell(productId: String): SellProductModel

    suspend fun postToRegisterProduct(request: SellRegisterRequestModel): SellRegisteredModel

    suspend fun getItemDetailInfo(itemId: String): SellInfoModel

    suspend fun getBuyerInfo(orderId: String): SellBuyerInfoModel

    suspend fun patchOrderConfirm(orderId: String): OrderConfirmModel

    suspend fun deleteSellingItem(itemId: String): Boolean
}
