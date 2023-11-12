package com.freetalk.domain.usecase

import com.freetalk.domain.entity.ChatRoomListEntity
import com.freetalk.domain.repository.ChatDataRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LoadRealTimeChatRoomListUseCase @Inject constructor(
    private val chatDataRepository: ChatDataRepository
) {
    operator fun invoke(): Flow<ChatRoomListEntity> {
        return chatDataRepository.loadRealTimeChatRoomList()
    }
}