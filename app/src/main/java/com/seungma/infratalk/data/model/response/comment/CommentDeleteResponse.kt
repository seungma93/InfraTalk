package com.seungma.infratalk.data.model.response.comment

import java.util.Date

data class CommentDeleteResponse(
    val commentAuthorEmail: String?,
    val commentCreateTime: Date?,
    val isSuccess: Boolean?
)