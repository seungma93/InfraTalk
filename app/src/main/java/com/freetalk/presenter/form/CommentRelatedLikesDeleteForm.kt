package com.freetalk.presenter.form

import java.util.Date

data class CommentRelatedLikesDeleteForm(
    val boardAuthorEmail: String,
    val boardCreateTime: Date
)