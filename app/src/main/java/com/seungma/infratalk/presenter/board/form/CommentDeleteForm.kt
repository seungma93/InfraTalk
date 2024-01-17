package com.seungma.infratalk.presenter.board.form

import java.util.Date

data class CommentDeleteForm(
    val commentAuthorEmail: String,
    val commentCreateTime: Date
)