package com.sm.infratalk.presenter.board.form

import java.util.Date

data class BoardLikeLoadForm(
    val boardAuthorEmail: String,
    val boardCreateTime: Date
)