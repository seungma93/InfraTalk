package com.sm.infratalk.presenter.board.form

import java.util.Date

data class CommentLikeDeleteForm(
    val commentAuthorEmail: String,
    val commentCreateTime: Date
)