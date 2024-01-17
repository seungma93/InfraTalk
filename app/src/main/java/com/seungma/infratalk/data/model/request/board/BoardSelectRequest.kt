package com.seungma.infratalk.data.model.request.board

import java.util.Date

data class BoardSelectRequest(
    val boardAuthorEmail: String,
    val boardCreateTime: Date
)