package com.sm.infratalk.domain.chat.repository

import com.sm.infratalk.domain.chat.entity.ChatMessageListEntity
import com.sm.infratalk.domain.chat.entity.ChatNotifyEntity
import com.sm.infratalk.domain.chat.entity.ChatMessageSendEntity
import com.sm.infratalk.domain.chat.entity.ChatRoomCheckEntity
import com.sm.infratalk.domain.chat.entity.ChatRoomCreateEntity
import com.sm.infratalk.domain.chat.entity.ChatRoomEntity
import com.sm.infratalk.domain.chat.entity.ChatRoomLeaveEntity
import com.sm.infratalk.domain.chat.entity.ChatRoomListEntity
import com.sm.infratalk.presenter.chat.form.ChatMessageListLoadForm
import com.sm.infratalk.presenter.chat.form.ChatMessageNotifyForm
import com.sm.infratalk.presenter.chat.form.ChatMessageSendForm
import com.sm.infratalk.presenter.chat.form.ChatRoomCheckForm
import com.sm.infratalk.presenter.chat.form.ChatRoomCreateForm
import com.sm.infratalk.presenter.chat.form.ChatRoomLeaveForm
import com.sm.infratalk.presenter.chat.form.ChatRoomLoadForm
import com.sm.infratalk.presenter.chat.form.RealTimeChatMessageLoadForm
import com.sm.infratalk.presenter.chat.form.RealTimeChatRoomLoadForm
import kotlinx.coroutines.flow.Flow

interface ChatDataRepository {
    suspend fun createChatRoom(chatRoomCreateForm: ChatRoomCreateForm): ChatRoomCreateEntity
    suspend fun checkChatRoom(chatRoomCheckForm: ChatRoomCheckForm): ChatRoomCheckEntity
    suspend fun sendChatMessage(chatMessageSendForm: ChatMessageSendForm): ChatMessageSendEntity
    suspend fun loadChatMessageList(chatMessageListLoadForm: ChatMessageListLoadForm): ChatMessageListEntity
    fun loadRealTimeChatMessage(realTimeChatMessageLoadForm: RealTimeChatMessageLoadForm): Flow<ChatMessageListEntity>
    suspend fun loadChatRoomList(): ChatRoomListEntity
    fun loadRealTimeChatRoomList(): Flow<ChatRoomListEntity>
    suspend fun loadChatRoom(chatRoomLoadForm: ChatRoomLoadForm): ChatRoomEntity
    suspend fun leaveChatRoom(chatRoomLeaveForm: ChatRoomLeaveForm): ChatRoomLeaveEntity
    fun loadRealTimeChatRoom(realTimeChatRoomLoadForm: RealTimeChatRoomLoadForm): Flow<ChatRoomEntity>
    fun notifyChatMessage(chatMessageNotifyForm: ChatMessageNotifyForm): Flow<ChatNotifyEntity>
}