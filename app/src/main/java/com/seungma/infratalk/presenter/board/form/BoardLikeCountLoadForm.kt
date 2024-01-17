package com.seungma.infratalk.presenter.board.form

import java.util.Date

data class BoardLikeCountLoadForm(
    val boardAuthorEmail: String,
    val boardCreateTime: Date
)