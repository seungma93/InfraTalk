package com.freetalk.domain.entity

data class ChatRoomCreateEntity(
    val member: List<String>,
    val isSuccess: Boolean
)

data class ChatRoomCheckEntity(
    val member: List<String>,
    val isChatRoom: Boolean
)

data class ChatStartEntity(
    val isSuccess: Boolean
)