package com.freetalk.data.model.response

import com.freetalk.domain.entity.ImagesResultEntity
import com.freetalk.domain.entity.UserEntity
import java.util.Date

data class BoardMetaResponse(
    val author: UserEntity? = null,
    val title: String? = null,
    val content: String? = null,
    val images: ImagesResultEntity? = null,
    val createTime: Date? = null,
    val editTime: Date? = null
)