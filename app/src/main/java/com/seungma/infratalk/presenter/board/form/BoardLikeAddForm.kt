package com.seungma.infratalk.presenter.board.form

import java.util.Date

data class BoardLikeAddForm(
    val boardAuthorEmail: String,
    val boardCreateTime: Date
)