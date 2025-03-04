package com.sm.infratalk.domain.chat.usecase

import com.sm.infratalk.domain.chat.entity.ChatRoomLeaveEntity
import com.sm.infratalk.domain.chat.repository.ChatDataRepository
import com.sm.infratalk.presenter.chat.form.ChatRoomLeaveForm
import javax.inject.Inject

class LeaveChatRoomUseCase @Inject constructor(private val chatDataRepository: ChatDataRepository) {
    suspend operator fun invoke(chatRoomLeaveForm: ChatRoomLeaveForm): ChatRoomLeaveEntity {
        return chatDataRepository.leaveChatRoom(chatRoomLeaveForm = chatRoomLeaveForm)
    }
}