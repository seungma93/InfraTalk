package com.seungma.infratalk.data.model.response.board

import com.seungma.infratalk.domain.image.ImagesResultEntity
import com.seungma.infratalk.domain.user.UserEntity
import java.util.Date

data class BoardMetaResponse(
    val author: UserEntity? = null,
    val title: String? = null,
    val content: String? = null,
    val images: ImagesResultEntity? = null,
    val createTime: Date? = null,
    val editTime: Date? = null
)