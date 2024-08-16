package co.orange.data.dto.response

import co.orange.domain.entity.response.ReissueTokenModel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ReissueTokenDto(
    @SerialName("accessToken")
    val accessToken: String,
    @SerialName("refreshToken")
    val refreshToken: String,
    @SerialName("userId")
    val userId: Long,
) {
    fun toModel() = ReissueTokenModel(accessToken = accessToken, refreshToken = refreshToken, userId = userId)
}