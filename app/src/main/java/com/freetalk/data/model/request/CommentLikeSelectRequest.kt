package com.freetalk.data.model.request

import java.util.Date

data class CommentLikeSelectRequest(
    val commentAuthorEmail: String,
    val commentCreateTime: Date
)