package com.freetalk.data.model.request

import java.util.Date

data class CommentBookmarkSelectRequest(
    val commentAuthorEmail: String,
    val commentCreateTime: Date
)