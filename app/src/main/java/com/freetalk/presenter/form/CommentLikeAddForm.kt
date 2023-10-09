package com.freetalk.presenter.form

import java.util.Date

data class CommentLikeAddForm(
    val commentAuthorEmail: String,
    val commentCreateTime: Date
)