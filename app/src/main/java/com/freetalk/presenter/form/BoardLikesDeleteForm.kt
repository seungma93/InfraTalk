package com.freetalk.presenter.form

import java.util.Date

data class BoardLikesDeleteForm(
    val boardAuthorEmail: String,
    val boardCreateTime: Date
)