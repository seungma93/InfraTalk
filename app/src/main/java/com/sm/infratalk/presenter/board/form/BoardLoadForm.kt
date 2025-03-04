package com.sm.infratalk.presenter.board.form

import java.util.Date

data class BoardLoadForm(
    val boardAuthorEmail: String,
    val boardCreateTime: Date
)