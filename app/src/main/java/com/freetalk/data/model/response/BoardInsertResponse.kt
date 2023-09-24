package com.freetalk.data.model.response

import java.util.Date

data class BoardInsertResponse(
    val boardAuthorEmail: String,
    val boardCreteTime: Date,
    val isSuccess: Boolean
)