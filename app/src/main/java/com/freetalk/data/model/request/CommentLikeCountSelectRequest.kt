package com.freetalk.data.model.request

import java.util.Date

data class CommentLikeCountSelectRequest(
    val commentAuthorEmail: String,
    val commentCreateTime: Date
)