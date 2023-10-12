package com.freetalk.domain.entity

import java.io.Serializable

data class ChatRoomCreateEntity(
    val member: List<String>,
    val chatRoomId: String?,
    val isSuccess: Boolean
)

data class ChatRoomCheckEntity(
    val member: List<String>,
    val chatRoomId: String?,
    val isChatRoom: Boolean
)

data class ChatStartEntity(
    val chatPartner: String,
    val chatRoomId: String?,
    val isSuccess: Boolean
)

data class ChatPrimaryKeyEntity(
    val partnerEmail: String,
    val chatRoomId: String
): Serializable