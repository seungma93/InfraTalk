package com.freetalk.presenter.form

import java.util.Date

data class CommentContentSelectForm(
    val boardAuthorEmail: String,
    val boardCreateTime: Date,
    val commentAuthorEmail: String,
    val commentCreateTime: Date
)