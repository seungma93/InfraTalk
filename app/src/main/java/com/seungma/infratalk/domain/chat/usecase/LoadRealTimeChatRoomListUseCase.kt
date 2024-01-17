package com.seungma.infratalk.domain.chat.usecase

import com.seungma.infratalk.domain.chat.entity.ChatRoomListEntity
import com.seungma.infratalk.domain.chat.repository.ChatDataRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LoadRealTimeChatRoomListUseCase @Inject constructor(
    private val chatDataRepository: ChatDataRepository
) {
    operator fun invoke(): Flow<ChatRoomListEntity> {
        return chatDataRepository.loadRealTimeChatRoomList()
    }
}