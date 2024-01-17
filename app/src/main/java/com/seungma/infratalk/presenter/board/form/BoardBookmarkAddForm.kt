package com.seungma.infratalk.presenter.board.form

import java.util.Date

data class BoardBookmarkAddForm(
    val boardAuthorEmail: String,
    val boardCreateTime: Date
)