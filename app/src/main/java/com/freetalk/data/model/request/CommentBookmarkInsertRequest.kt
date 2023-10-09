package com.freetalk.data.model.request

import java.util.Date

data class CommentBookmarkInsertRequest(
    val commentAuthorEmail: String,
    val commentCreateTime: Date,
    val userEmail: String,
    val updateTime: Date
)