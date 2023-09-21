package com.freetalk.presenter.form

import java.util.Date

data class CommentDeleteForm(
    val commentAuthorEmail: String,
    val commentCreateTIme: Date
)