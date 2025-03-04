package com.sm.infratalk.presenter.board.form

import java.util.Date

data class CommentMetaListLoadForm(
    val boardAuthorEmail: String,
    val boardCreateTime: Date,
    val reload: Boolean
)