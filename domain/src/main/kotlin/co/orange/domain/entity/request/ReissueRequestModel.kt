package co.orange.domain.entity.request

data class ReissueRequestModel(
    val accessToken: String,
    val refreshToken: String,
)
