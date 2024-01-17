package com.seungma.infratalk.data.model.response.board

import java.util.Date

data class BoardDeleteResponse(
    val boardAuthorEmail: String?,
    val boardCreateTime: Date?,
    val isSuccess: Boolean?
)