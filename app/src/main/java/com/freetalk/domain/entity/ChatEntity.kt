package com.freetalk.domain.entity

import com.freetalk.data.model.response.UserResponse
import java.io.Serializable
import java.util.Date

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

data class ChatMessageSendEntity(
    val senderEmail: String,
    val sendTime: Date,
    val content: String,
    val isSuccess: Boolean
)

data class ChatMessageSend(
    val isSuccess: Boolean
)

data class ChatMessageEntity(
    val sender: UserEntity,
    val sendTime: Date,
    val content: String,
    val chatRoomId: String
)
data class ChatMessageListEntity(
    val chatMessageList: List<ChatMessageEntity>
)