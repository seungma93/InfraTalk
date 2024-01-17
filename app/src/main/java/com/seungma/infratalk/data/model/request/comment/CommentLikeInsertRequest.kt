package com.seungma.infratalk.data.model.request.comment

import java.util.Date

data class CommentLikeInsertRequest(
    val commentAuthorEmail: String,
    val commentCreateTime: Date,
    val userEmail: String,
    val updateTime: Date
)