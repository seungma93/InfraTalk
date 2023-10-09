package com.freetalk.presenter.form

import java.util.Date

data class CommentLikeDeleteForm(
    val commentAuthorEmail: String,
    val commentCreateTime: Date
)