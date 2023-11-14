package com.freetalk.data.model.request

import java.util.Date

data class BoardDeleteRequest(
    val boardAuthorEmail: String,
    val boardCreateTime: Date
)