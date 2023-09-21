package com.freetalk.data.model.request

import java.util.Date

data class BoardSelectRequest(
    val boardAuthorEmail: String,
    val boardCreateTime: Date
)