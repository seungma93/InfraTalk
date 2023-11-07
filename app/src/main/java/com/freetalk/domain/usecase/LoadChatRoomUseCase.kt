package com.freetalk.domain.usecase

import com.freetalk.domain.entity.ChatRoomEntity
import com.freetalk.domain.entity.ChatRoomListEntity
import com.freetalk.domain.repository.ChatDataRepository
import com.freetalk.presenter.form.ChatRoomLoadForm
import javax.inject.Inject

class LoadChatRoomUseCase @Inject constructor(private val chatDataRepository: ChatDataRepository) {
    suspend operator fun invoke(chatRoomLoadForm: ChatRoomLoadForm): ChatRoomEntity {
        return chatDataRepository.loadChatRoom(chatRoomLoadForm = chatRoomLoadForm)
    }
}