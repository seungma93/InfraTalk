package com.freetalk.domain.usecase

import com.freetalk.domain.entity.ChatRoomListEntity
import com.freetalk.domain.repository.ChatDataRepository
import javax.inject.Inject

class LoadChatRoomListUseCase @Inject constructor(private val chatDataRepository: ChatDataRepository){
    suspend operator fun invoke(): ChatRoomListEntity {
        return chatDataRepository.loadChatRoomList()
    }
}