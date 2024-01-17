package com.seungma.infratalk.data.model.request.comment

import java.util.Date

data class CommentDeleteRequest(
    val commentAuthorEmail: String,
    val commentCreateTime: Date
)