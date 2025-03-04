package com.sm.infratalk.data.model.request.board

import java.util.Date

data class BoardInsertRequest(
    val authorEmail: String,
    val title: String,
    val content: String,
    val editTime: Date?,
    val createTime: Date = Date(System.currentTimeMillis())
)