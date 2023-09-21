package com.freetalk.presenter.form

import java.util.Date

data class CommentBookmarkLoadForm(
    val commentAuthorEmail: String,
    val commentCreateTime: Date
)