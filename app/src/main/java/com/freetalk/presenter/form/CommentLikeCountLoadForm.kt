package com.freetalk.presenter.form

import java.util.Date

data class CommentLikeCountLoadForm(
    val commentAuthorEmail: String,
    val commentCreateTime: Date
)