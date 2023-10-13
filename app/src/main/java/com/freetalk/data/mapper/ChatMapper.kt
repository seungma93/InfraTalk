package com.freetalk.data.mapper

import com.freetalk.data.model.response.ChatMessageSendResponse
import com.freetalk.data.model.response.ChatRoomCheckResponse
import com.freetalk.data.model.response.ChatRoomCreateResponse
import com.freetalk.domain.entity.ChatMessageSendEntity
import com.freetalk.domain.entity.ChatRoomCheckEntity
import com.freetalk.domain.entity.ChatRoomCreateEntity
import java.util.Date

fun ChatRoomCreateResponse.toEntity(): ChatRoomCreateEntity {
    return ChatRoomCreateEntity(
        member = member.orEmpty(),
        chatRoomId = chatRoomId.orEmpty(),
        isSuccess = isSuccess ?: false
    )
}

fun ChatRoomCheckResponse.toEntity(): ChatRoomCheckEntity {
    return ChatRoomCheckEntity(
        member = member.orEmpty(),
        chatRoomId = chatRoomId.orEmpty(),
        isChatRoom = isChatRoom ?: false
    )
}

fun ChatMessageSendResponse.toEntity(): ChatMessageSendEntity {
    return ChatMessageSendEntity(
        senderEmail = senderEmail.orEmpty(),
        sendTime = sendTime ?: Date(),
        content = content.orEmpty(),
        isSuccess = isSuccess ?: false
    )
}