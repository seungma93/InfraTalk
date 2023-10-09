package com.freetalk.data.model.request

import java.util.Date

data class BoardBookmarkSelectRequest(
    val boardAuthorEmail: String,
    val boardCreateTime: Date
)