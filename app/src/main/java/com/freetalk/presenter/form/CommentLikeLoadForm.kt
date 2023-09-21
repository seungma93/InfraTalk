package com.freetalk.presenter.form

import java.util.Date

data class CommentLikeLoadForm(
    val commentAuthorEmail: String,
    val commentCreateTime: Date
)