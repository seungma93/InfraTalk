package com.freetalk.data.model.request

import java.util.Date

data class BoardRelatedAllCommentMetaListSelectRequest(
    val boardAuthorEmail: String,
    val boardCreateTime: Date
)