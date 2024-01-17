package com.seungma.infratalk.data.model.request.comment

import java.util.Date

data class CommentBookmarkSelectRequest(
    val commentAuthorEmail: String,
    val commentCreateTime: Date
)