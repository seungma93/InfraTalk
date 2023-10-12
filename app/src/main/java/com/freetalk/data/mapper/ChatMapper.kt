package com.freetalk.data.mapper

import com.freetalk.data.model.response.ChatRoomCheckResponse
import com.freetalk.data.model.response.ChatRoomCreateResponse
import com.freetalk.domain.entity.ChatRoomCheckEntity
import com.freetalk.domain.entity.ChatRoomCreateEntity

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