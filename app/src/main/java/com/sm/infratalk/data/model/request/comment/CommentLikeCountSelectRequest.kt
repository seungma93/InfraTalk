package com.sm.infratalk.data.model.request.comment

import java.util.Date

data class CommentLikeCountSelectRequest(
    val commentAuthorEmail: String,
    val commentCreateTime: Date
)