package com.freetalk.presenter.form

import java.util.Date

data class CommentBookmarkAddForm(
    val commentAuthorEmail: String,
    val commentCreateTime: Date
)