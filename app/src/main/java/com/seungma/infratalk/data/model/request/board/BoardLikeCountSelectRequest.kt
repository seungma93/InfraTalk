package com.seungma.infratalk.data.model.request.board

import java.util.Date

data class BoardLikeCountSelectRequest(
    val boardAuthorEmail: String,
    val boardCreateTime: Date
)
