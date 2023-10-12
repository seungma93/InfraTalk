package com.freetalk.data.model.response

data class ChatRoomCreateResponse(
    val member: List<String>?,
    val chatRoomId: String?,
    val isSuccess: Boolean?
)