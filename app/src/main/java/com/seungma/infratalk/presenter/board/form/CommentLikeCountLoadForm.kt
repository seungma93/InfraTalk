package com.seungma.infratalk.presenter.board.form

import java.util.Date

data class CommentLikeCountLoadForm(
    val commentAuthorEmail: String,
    val commentCreateTime: Date
)