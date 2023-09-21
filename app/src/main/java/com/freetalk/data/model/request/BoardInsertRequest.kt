package com.freetalk.data.model.request

import com.freetalk.domain.entity.UserEntity
import java.util.Date

data class BoardInsertRequest (
    val author: UserEntity,
    val title: String,
    val content: String,
    val createTime: Date,
    val editTime: Date?
)