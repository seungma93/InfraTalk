package com.freetalk.data.model.request

import java.util.Date

data class CommentBookmarkDeleteRequest(
    val commentAuthorEmail: String,
    val commentCreateTime: Date
)


