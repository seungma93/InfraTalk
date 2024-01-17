package com.seungma.infratalk.data.model.request.comment

import java.util.Date

data class CommentRelatedLikesDeleteRequest(
    val commentAuthorEmail: String,
    val commentCreateTime: Date
)