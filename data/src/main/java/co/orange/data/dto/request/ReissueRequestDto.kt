package co.orange.data.dto.request

import co.orange.domain.entity.request.ReissueRequestModel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ReissueRequestDto(
    @SerialName("refreshtoken")
    val refreshtoken: String,
) {
    companion object {
        fun ReissueRequestModel.toDto() = ReissueRequestDto(refreshtoken)
    }
}
