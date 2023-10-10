package com.freetalk.domain.entity

import java.io.Serializable

data class ChatRoomCreateEntity(
    val member: List<String>,
    val isSuccess: Boolean
)

data class ChatRoomCheckEntity(
    val member: List<String>,
    val isChatRoom: Boolean
)

data class ChatStartEntity(
    val chatPartner: String,
    val isSuccess: Boolean
)

data class ChatPartnerEntity(
    val partnerEmail: String
): Serializable