package com.seungma.infratalk.data.model.request.comment

import java.util.Date

data class CommentLikeDeleteRequest(
    val commentAuthorEmail: String,
    val commentCreateTime: Date
)