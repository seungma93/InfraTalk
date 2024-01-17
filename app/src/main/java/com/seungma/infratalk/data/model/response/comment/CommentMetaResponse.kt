package com.seungma.infratalk.data.model.response.comment

import com.seungma.infratalk.data.model.response.user.UserResponse
import com.seungma.infratalk.domain.image.ImagesResultEntity
import java.util.Date

data class CommentMetaResponse(
    val author: UserResponse? = null,
    val createTime: Date? = null,
    val content: String? = null,
    val images: ImagesResultEntity? = null,
    val boardAuthorEmail: String? = null,
    val boardCreateTime: Date? = null,
    val editTime: Date? = null,
    val isLastPage: Boolean? = null,
)