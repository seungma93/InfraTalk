package com.freetalk.data.model.request

import java.util.Date

data class BoardLikeSelectRequest(
    val boardAuthorEmail: String,
    val boardCreateTime: Date
)