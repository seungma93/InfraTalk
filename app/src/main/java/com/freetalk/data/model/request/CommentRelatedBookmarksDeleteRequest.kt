package com.freetalk.data.model.request

import java.util.Date

data class CommentRelatedBookmarksDeleteRequest(
    val commentAuthorEmail: String,
    val commentCreateTime: Date
)