package com.freetalk.presenter.form

import java.util.Date

data class CommentRelatedBookmarksDeleteForm(
    val commentAuthorEmail: String,
    val commentCreateTime: Date
)