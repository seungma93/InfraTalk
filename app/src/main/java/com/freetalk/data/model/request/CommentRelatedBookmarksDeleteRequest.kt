package com.freetalk.data.model.request

import java.util.Date

data class CommentRelatedBookmarksDeleteRequest(
    val boardAuthorEmail: String,
    val boardCreateTime: Date
)