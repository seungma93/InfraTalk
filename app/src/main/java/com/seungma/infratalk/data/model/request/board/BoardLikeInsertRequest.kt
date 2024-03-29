package com.seungma.infratalk.data.model.request.board

import java.util.Date

data class BoardLikeInsertRequest(
    val boardAuthorEmail: String,
    val boardCreateTime: Date,
    val userEmail: String,
    val updateTime: Date
)