package com.sm.infratalk.presenter.board.form

import java.util.Date

data class CommentListLoadForm(
    val boardAuthorEmail: String,
    val boardCreateTime: Date,
    val reload: Boolean
)