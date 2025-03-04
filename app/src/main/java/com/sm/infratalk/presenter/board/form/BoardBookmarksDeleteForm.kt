package com.sm.infratalk.presenter.board.form

import java.util.Date

data class BoardBookmarksDeleteForm(
    val boardAuthorEmail: String,
    val boardCreateTime: Date
)