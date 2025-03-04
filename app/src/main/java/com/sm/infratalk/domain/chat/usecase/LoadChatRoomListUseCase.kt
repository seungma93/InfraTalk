package com.sm.infratalk.domain.chat.usecase

import com.sm.infratalk.domain.chat.entity.ChatRoomListEntity
import com.sm.infratalk.domain.chat.repository.ChatDataRepository
import javax.inject.Inject

class LoadChatRoomListUseCase @Inject constructor(private val chatDataRepository: ChatDataRepository) {
    suspend operator fun invoke(): ChatRoomListEntity {
        return chatDataRepository.loadChatRoomList()
    }
}