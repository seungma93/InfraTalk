package com.freetalk.presenter.form

import java.util.Date

data class BoardLikeCountLoadForm(
    val boardAuthorEmail: String,
    val boardCreateTime: Date
)