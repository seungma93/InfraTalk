package com.freetalk.presenter.form

import java.util.Date

data class CommentInsertForm(
    val boardAuthorEmail: String,
    val boardCreateTime: Date,
    val content: String
)