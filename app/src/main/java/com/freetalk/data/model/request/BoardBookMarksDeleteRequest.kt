package com.freetalk.data.model.request

import java.util.Date

data class BoardBookMarksDeleteRequest(
    val boardAuthorEmail: String,
    val boardCreateTime: Date
)