package com.sm.infratalk.data.model.request.board

import java.util.Date

data class BoardBookMarksDeleteRequest(
    val boardAuthorEmail: String,
    val boardCreateTime: Date
)