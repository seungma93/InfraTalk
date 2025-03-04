package com.sm.infratalk.presenter.board.form

import java.util.Date

data class BoardLikeAddForm(
    val boardAuthorEmail: String,
    val boardCreateTime: Date
)