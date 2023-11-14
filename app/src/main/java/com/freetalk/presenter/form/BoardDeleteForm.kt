package com.freetalk.presenter.form

import java.util.Date

data class BoardDeleteForm(
    val boardAuthorEmail: String,
    val boardCreateTime: Date
)