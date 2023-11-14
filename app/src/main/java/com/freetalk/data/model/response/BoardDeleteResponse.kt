package com.freetalk.data.model.response

import java.util.Date

data class BoardDeleteResponse(
    val boardAuthorEmail: String?,
    val boardCreateTime: Date?,
    val isSuccess: Boolean?
)