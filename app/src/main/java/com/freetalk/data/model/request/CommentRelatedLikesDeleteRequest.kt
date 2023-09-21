package com.freetalk.data.model.request

import java.util.Date

data class CommentRelatedLikesDeleteRequest(
    val boardAuthorEmail: String,
    val boardCreateTime: Date
)