package com.freetalk.presenter.form

import com.freetalk.domain.entity.UserEntity
import java.util.Date

data class CommentInsertRequest(
    val authorEmail: String,
    val createTime: Date,
    val content: String,
    val boardAuthorEmail: String,
    val boardCreateTime: Date,
    val editTime: Date
)