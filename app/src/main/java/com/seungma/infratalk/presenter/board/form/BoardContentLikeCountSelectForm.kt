package com.seungma.infratalk.presenter.board.form

import java.util.Date

data class BoardContentLikeCountSelectForm(
    val boardAuthorEmail: String,
    val boardCreateTime: Date
)