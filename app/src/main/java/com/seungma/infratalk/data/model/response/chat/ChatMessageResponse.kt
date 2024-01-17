package com.seungma.infratalk.data.model.response.chat

import com.seungma.infratalk.data.model.response.user.UserResponse
import java.util.Date

data class ChatMessageResponse(
    val sender: UserResponse?,
    val sendTime: Date?,
    val content: String?,
    val chatRoomId: String?,
    val isLastPage: Boolean?
)