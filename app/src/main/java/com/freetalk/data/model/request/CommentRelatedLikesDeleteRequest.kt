package com.freetalk.data.model.request

import java.util.Date

data class CommentRelatedLikesDeleteRequest(
    val commentAuthorEmail: String,
    val commentCreateTime: Date
)