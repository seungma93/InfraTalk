package com.seungma.infratalk.data.model.request.comment

import java.util.Date

data class CommentMetaListSelectRequest(
    val boardAuthorEmail: String,
    val boardCreateTime: Date,
    val reload: Boolean
)