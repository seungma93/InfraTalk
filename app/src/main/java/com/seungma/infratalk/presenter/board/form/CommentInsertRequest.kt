package com.seungma.infratalk.presenter.board.form

import java.util.Date

data class CommentInsertRequest(
    val authorEmail: String,
    val createTime: Date,
    val content: String,
    val boardAuthorEmail: String,
    val boardCreateTime: Date,
    val editTime: Date
)