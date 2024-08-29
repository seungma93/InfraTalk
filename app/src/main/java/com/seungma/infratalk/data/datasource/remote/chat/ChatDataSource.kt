package com.seungma.infratalk.data.datasource.remote.chat

import com.seungma.infratalk.data.model.request.chat.ChatMessageListLoadRequest
import com.seungma.infratalk.data.model.request.chat.ChatMessageSendRequest
import com.seungma.infratalk.data.model.request.chat.ChatRoomCheckRequest
import com.seungma.infratalk.data.model.request.chat.ChatRoomCreateRequest
import com.seungma.infratalk.data.model.request.chat.ChatRoomLeaveRequest
import com.seungma.infratalk.data.model.request.chat.ChatRoomLoadRequest
import com.seungma.infratalk.data.model.request.chat.RealTimeChatMessageLoadRequest
import com.seungma.infratalk.data.model.request.chat.RealTimeChatRoomLoadRequest
import com.seungma.infratalk.data.model.response.chat.ChatMessageListResponse
import com.seungma.infratalk.data.model.response.chat.ChatMessageSendResponse
import com.seungma.infratalk.data.model.response.chat.ChatRoomCheckResponse
import com.seungma.infratalk.data.model.response.chat.ChatRoomCreateResponse
import com.seungma.infratalk.data.model.response.chat.ChatRoomLeaveResponse
import com.seungma.infratalk.data.model.response.chat.ChatRoomListResponse
import com.seungma.infratalk.data.model.response.chat.ChatRoomResponse
import kotlinx.coroutines.flow.Flow

interface ChatDataSource {
    suspend fun createChatRoom(chatRoomCreateRequest: ChatRoomCreateRequest): ChatRoomCreateResponse
    suspend fun checkChatRoom(chatRoomCheckRequest: ChatRoomCheckRequest): ChatRoomCheckResponse
    suspend fun sendChatMessage(chatMessageSendRequest: ChatMessageSendRequest): ChatMessageSendResponse
    suspend fun loadChatMessageList(chatMessageListLoadRequest: ChatMessageListLoadRequest): ChatMessageListResponse
    fun loadRealTimeChatMessage(realTimeChatMessageLoadRequest: RealTimeChatMessageLoadRequest): Flow<ChatMessageListResponse>
    suspend fun loadChatRoomList(): ChatRoomListResponse
    fun loadRealTimeChatRoomList(): Flow<ChatRoomListResponse>
    suspend fun loadChatRoom(chatRoomLoadRequest: ChatRoomLoadRequest): ChatRoomResponse
    suspend fun leaveChatRoom(chatRoomLeaveRequest: ChatRoomLeaveRequest): ChatRoomLeaveResponse
    fun loadRealTimeChatRoom(realTimeChatRoomLoadRequest: RealTimeChatRoomLoadRequest): Flow<ChatRoomResponse>
}