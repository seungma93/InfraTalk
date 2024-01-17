package com.seungma.infratalk.presenter.board.form

import java.util.Date

data class CommentMetaListLoadForm(
    val boardAuthorEmail: String,
    val boardCreateTime: Date,
    val reload: Boolean
)