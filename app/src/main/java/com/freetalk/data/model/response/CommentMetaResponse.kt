package com.freetalk.data.model.response

import com.freetalk.domain.entity.ImagesResultEntity
import java.util.Date

data class CommentMetaResponse(
    val author: UserResponse? = null,
    val createTime: Date? = null,
    val content: String? = null,
    val images: ImagesResultEntity? = null,
    val boardAuthorEmail: String? = null,
    val boardCreateTime: Date? = null,
    val editTime: Date? = null,
    val isPage: Boolean? = null,
)