package com.seungma.infratalk.data.model.response.board

import com.seungma.infratalk.data.model.response.user.UserResponse
import com.seungma.infratalk.domain.image.ImagesResultEntity
import java.util.Date

data class BoardMetaResponse(
    val author: UserResponse? = null,
    val title: String? = null,
    val content: String? = null,
    val images: ImagesResultEntity? = null,
    val createTime: Date? = null,
    val editTime: Date? = null
)