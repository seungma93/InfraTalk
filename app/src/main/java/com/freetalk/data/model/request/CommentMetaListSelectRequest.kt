package com.freetalk.data.model.request

import java.util.Date

data class CommentMetaListSelectRequest(
    val boardAuthorEmail: String,
    val boardCreateTime: Date,
    val reload: Boolean
)