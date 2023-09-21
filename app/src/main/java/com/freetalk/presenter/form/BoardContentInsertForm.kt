package com.freetalk.presenter.form

import com.freetalk.domain.entity.UserEntity
import java.util.Date

data class BoardContentInsertForm(
    val author: UserEntity,
    val title: String,
    val content: String,
    val createTime: Date,
    val editTime: Date?
)