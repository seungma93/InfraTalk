package com.seungma.infratalk.presenter.board.form

import java.util.Date

data class BoardLikeDeleteForm(
    val boardAuthorEmail: String,
    val boardCreateTime: Date
)