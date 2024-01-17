package com.seungma.infratalk.data.model.response.chat

data class ChatRoomCreateResponse(
    val member: List<String>?,
    val chatRoomId: String?,
    val isSuccess: Boolean?
)