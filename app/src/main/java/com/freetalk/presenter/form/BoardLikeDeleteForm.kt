package com.freetalk.presenter.form

import java.util.Date

data class BoardLikeDeleteForm(
    val boardAuthorEmail: String,
    val boardCreateTime: Date
)