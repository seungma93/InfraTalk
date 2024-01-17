package com.seungma.infratalk.presenter.board.form

import java.util.Date

data class BoardRelatedAllCommentMetaListSelectForm(
    val boardAuthorEmail: String,
    val boardCreateTime: Date
)