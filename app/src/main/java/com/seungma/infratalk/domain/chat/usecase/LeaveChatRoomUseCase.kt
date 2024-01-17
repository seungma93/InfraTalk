package com.seungma.infratalk.domain.chat.usecase

import com.seungma.infratalk.domain.chat.entity.ChatRoomLeaveEntity
import com.seungma.infratalk.domain.chat.repository.ChatDataRepository
import com.seungma.infratalk.presenter.chat.form.ChatRoomLeaveForm
import javax.inject.Inject

class LeaveChatRoomUseCase @Inject constructor(private val chatDataRepository: ChatDataRepository) {
    suspend operator fun invoke(chatRoomLeaveForm: ChatRoomLeaveForm): ChatRoomLeaveEntity {
        return chatDataRepository.leaveChatRoom(chatRoomLeaveForm = chatRoomLeaveForm)
    }
}