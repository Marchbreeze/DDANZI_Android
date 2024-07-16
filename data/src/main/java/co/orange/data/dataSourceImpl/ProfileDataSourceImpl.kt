package co.orange.data.dataSourceImpl

import co.orange.data.dataSource.ProfileDataSource
import co.orange.data.dto.BaseResponse
import co.orange.data.dto.response.ProfileInterestDto
import co.orange.data.dto.response.ProfileNicknameDto
import co.orange.data.service.ProfileService
import javax.inject.Inject

data class ProfileDataSourceImpl
    @Inject
    constructor(
        private val profileService: ProfileService,
    ) : ProfileDataSource {
        override suspend fun getNickname(): BaseResponse<ProfileNicknameDto> = profileService.getNickname()

        override suspend fun getInterestList(): BaseResponse<ProfileInterestDto> = profileService.getInterestList()
    }