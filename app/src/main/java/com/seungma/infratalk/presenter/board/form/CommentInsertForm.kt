package com.seungma.infratalk.presenter.board.form

import java.util.Date

data class CommentInsertForm(
    val boardAuthorEmail: String,
    val boardCreateTime: Date,
    val content: String
)