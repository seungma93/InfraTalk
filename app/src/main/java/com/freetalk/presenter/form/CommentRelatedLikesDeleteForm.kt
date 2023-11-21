package com.freetalk.presenter.form

import java.util.Date

data class CommentRelatedLikesDeleteForm(
    val commentAuthorEmail: String,
    val commentCreateTime: Date
)