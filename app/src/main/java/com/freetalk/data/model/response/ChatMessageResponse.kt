package com.freetalk.data.model.response

import com.freetalk.domain.entity.UserEntity
import java.util.Date

data class ChatMessageResponse(
    val sender: UserResponse?,
    val sendTime: Date?,
    val content: String?,
    val chatRoomId: String?,
    val isLastPage: Boolean?
)