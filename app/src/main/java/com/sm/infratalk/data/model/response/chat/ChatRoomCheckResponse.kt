package com.sm.infratalk.data.model.response.chat

data class ChatRoomCheckResponse(
    val member: List<String>?,
    val chatRoomId: String?,
    val isChatRoom: Boolean?
)