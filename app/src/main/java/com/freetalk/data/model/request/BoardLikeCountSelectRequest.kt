package com.freetalk.data.model.request

import java.util.Date
import java.util.SimpleTimeZone

data class BoardLikeCountSelectRequest(
    val boardAuthorEmail: String,
    val boardCreateTime: Date
)
