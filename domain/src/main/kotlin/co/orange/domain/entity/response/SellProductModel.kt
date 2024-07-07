package co.orange.domain.entity.response

data class SellProductModel(
    val productId: Long,
    val productName: String,
    val originPrice: Int,
    val salePrice: Int,
)