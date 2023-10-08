package com.freetalk.data.model.response

data class ChatRoomCheckResponse(
    val member: List<String>?,
    val isChatRoom: Boolean?
)