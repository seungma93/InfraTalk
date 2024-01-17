package com.seungma.infratalk.presenter.board.form

import java.util.Date

data class CommentBookmarkDeleteForm(
    val commentAuthorEmail: String,
    val commentCreateTime: Date
)