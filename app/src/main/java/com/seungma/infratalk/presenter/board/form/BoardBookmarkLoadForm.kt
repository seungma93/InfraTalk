package com.seungma.infratalk.presenter.board.form

import java.util.Date

data class BoardBookmarkLoadForm(
    val boardAuthorEmail: String,
    val boardCreateTime: Date
)