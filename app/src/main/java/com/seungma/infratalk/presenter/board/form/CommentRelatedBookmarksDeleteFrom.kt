package com.seungma.infratalk.presenter.board.form

import java.util.Date

data class CommentRelatedBookmarksDeleteForm(
    val commentAuthorEmail: String,
    val commentCreateTime: Date
)