package com.freetalk.data.model.request

import java.util.Date

data class BoardBookmarkDeleteRequest(
    val boardAuthorEmail: String,
    val boardCreateTime: Date
)