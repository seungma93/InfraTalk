package com.sm.infratalk.presenter.board.form

import java.util.Date

data class CommentLikeAddForm(
    val commentAuthorEmail: String,
    val commentCreateTime: Date
)