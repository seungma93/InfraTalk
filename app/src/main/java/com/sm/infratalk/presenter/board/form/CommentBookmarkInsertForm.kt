package com.sm.infratalk.presenter.board.form

import java.util.Date

data class CommentBookmarkInsertForm(
    val commentAuthorEmail: String,
    val commentCreateTime: Date
)