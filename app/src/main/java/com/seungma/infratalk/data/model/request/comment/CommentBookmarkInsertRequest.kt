package com.seungma.infratalk.data.model.request.comment

import java.util.Date

data class CommentBookmarkInsertRequest(
    val commentAuthorEmail: String,
    val commentCreateTime: Date,
    val userEmail: String,
    val updateTime: Date
)