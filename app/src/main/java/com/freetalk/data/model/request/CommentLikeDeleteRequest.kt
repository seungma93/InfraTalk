package com.freetalk.data.model.request

import java.util.Date

data class CommentLikeDeleteRequest(
    val commentAuthorEmail: String,
    val commentCreateTime: Date
)