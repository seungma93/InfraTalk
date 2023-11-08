package com.freetalk.domain.repository

import com.freetalk.domain.entity.ChatMessageListEntity
import com.freetalk.domain.entity.ChatMessageSendEntity
import com.freetalk.domain.entity.ChatRoomCheckEntity
import com.freetalk.domain.entity.ChatRoomCreateEntity
import com.freetalk.domain.entity.ChatRoomEntity
import com.freetalk.domain.entity.ChatRoomLeaveEntity
import com.freetalk.domain.entity.ChatRoomListEntity
import com.freetalk.presenter.form.ChatMessageListLoadForm
import com.freetalk.presenter.form.ChatMessageSendForm
import com.freetalk.presenter.form.ChatRoomCheckForm
import com.freetalk.presenter.form.ChatRoomCreateForm
import com.freetalk.presenter.form.ChatRoomLeaveForm
import com.freetalk.presenter.form.ChatRoomLoadForm
import com.freetalk.presenter.form.RealTimeChatMessageLoadForm
import kotlinx.coroutines.flow.Flow

interface ChatDataRepository {
    suspend fun createChatRoom(chatRoomCreateForm: ChatRoomCreateForm): ChatRoomCreateEntity
    suspend fun checkChatRoom(chatRoomCheckForm: ChatRoomCheckForm): ChatRoomCheckEntity
    suspend fun sendChatMessage(chatMessageSendForm: ChatMessageSendForm): ChatMessageSendEntity
    suspend fun loadChatMessageList(chatMessageListLoadForm: ChatMessageListLoadForm): ChatMessageListEntity
    fun loadRealTimeChatMessage(realTimeChatMessageLoadForm: RealTimeChatMessageLoadForm): Flow<ChatMessageListEntity>
    suspend fun loadChatRoomList(): ChatRoomListEntity
    fun loadRealTimeChatRoom(): Flow<ChatRoomListEntity>
    suspend fun loadChatRoom(chatRoomLoadForm: ChatRoomLoadForm): ChatRoomEntity
    suspend fun leaveChatRoom(chatRoomLeaveForm: ChatRoomLeaveForm): ChatRoomLeaveEntity
}