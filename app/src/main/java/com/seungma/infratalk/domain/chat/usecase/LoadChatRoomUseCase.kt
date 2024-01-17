package com.seungma.infratalk.domain.chat.usecase

import com.seungma.infratalk.domain.chat.entity.ChatRoomEntity
import com.seungma.infratalk.domain.chat.repository.ChatDataRepository
import com.seungma.infratalk.presenter.chat.form.ChatRoomLoadForm
import javax.inject.Inject

class LoadChatRoomUseCase @Inject constructor(private val chatDataRepository: ChatDataRepository) {
    suspend operator fun invoke(chatRoomLoadForm: ChatRoomLoadForm): ChatRoomEntity {
        return chatDataRepository.loadChatRoom(chatRoomLoadForm = chatRoomLoadForm)
    }
}