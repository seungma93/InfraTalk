package com.freetalk.data.model.response

import java.util.Date

data class CommentDeleteResponse(
    val commentAuthorEmail: String?,
    val commentCreateTime: Date?,
    val isSuccess: Boolean?
)