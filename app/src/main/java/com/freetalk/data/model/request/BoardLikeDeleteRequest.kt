package com.freetalk.data.model.request

import java.util.Date

data class BoardLikeDeleteRequest(
    val boardAuthorEmail: String,
    val boardCreateTime: Date
)