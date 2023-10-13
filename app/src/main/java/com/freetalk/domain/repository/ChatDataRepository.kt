package com.freetalk.domain.repository

import com.freetalk.domain.entity.ChatMessageSendEntity
import com.freetalk.domain.entity.ChatRoomCheckEntity
import com.freetalk.domain.entity.ChatRoomCreateEntity
import com.freetalk.presenter.form.ChatMessageSendForm
import com.freetalk.presenter.form.ChatRoomCheckForm
import com.freetalk.presenter.form.ChatRoomCreateForm

interface ChatDataRepository {
    suspend fun createChatRoom(chatRoomCreateForm: ChatRoomCreateForm): ChatRoomCreateEntity
    suspend fun checkChatRoom(chatRoomCheckForm: ChatRoomCheckForm): ChatRoomCheckEntity
    suspend fun sendChatMessage(chatMessageSendForm: ChatMessageSendForm): ChatMessageSendEntity
}