package com.freetalk.data.model.request

import com.freetalk.domain.entity.UserEntity
import java.util.Date

data class BoardInsertRequest (
    val authorEmail: String,
    val createTime: Date,
    val title: String,
    val content: String,
    val editTime: Date?
)