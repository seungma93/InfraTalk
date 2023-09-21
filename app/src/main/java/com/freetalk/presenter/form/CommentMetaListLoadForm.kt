package com.freetalk.presenter.form

import java.util.Date

data class CommentMetaListLoadForm(
    val boardAuthorEmail: String,
    val boardCreateTime: Date,
    val reload: Boolean
)