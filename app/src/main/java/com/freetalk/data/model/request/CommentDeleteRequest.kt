package com.freetalk.data.model.request

import java.util.Date

data class CommentDeleteRequest(
    val commentAuthorEmail: String,
    val commentCreateTime: Date
)