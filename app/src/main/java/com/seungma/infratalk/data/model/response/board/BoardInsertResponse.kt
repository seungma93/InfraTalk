package com.seungma.infratalk.data.model.response.board

import java.util.Date

data class BoardInsertResponse(
    val boardAuthorEmail: String?,
    val boardCreteTime: Date?,
    val isSuccess: Boolean?
)