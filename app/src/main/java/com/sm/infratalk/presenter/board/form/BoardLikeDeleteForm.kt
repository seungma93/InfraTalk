package com.sm.infratalk.presenter.board.form

import java.util.Date

data class BoardLikeDeleteForm(
    val boardAuthorEmail: String,
    val boardCreateTime: Date
)