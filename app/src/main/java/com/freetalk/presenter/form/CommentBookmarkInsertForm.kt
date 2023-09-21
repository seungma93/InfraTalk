package com.freetalk.presenter.form

import java.util.Date

data class CommentBookmarkInsertForm(
    val commentAuthorEmail: String,
    val commentCreateTime: Date
)