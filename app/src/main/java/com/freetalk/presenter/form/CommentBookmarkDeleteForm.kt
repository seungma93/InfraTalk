package com.freetalk.presenter.form

import java.util.Date

data class CommentBookmarkDeleteForm(
    val commentAuthorEmail: String,
    val commentCreateTime: Date
)