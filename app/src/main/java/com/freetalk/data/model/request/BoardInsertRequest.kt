package com.freetalk.data.model.request

import java.util.Date

data class BoardInsertRequest (
    val authorEmail: String,
    val title: String,
    val content: String,
    val editTime: Date?
) {
    val createTime: Date get() = Date(System.currentTimeMillis())
}