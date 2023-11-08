package com.freetalk.domain.usecase

import com.freetalk.domain.entity.ChatRoomLeaveEntity
import com.freetalk.domain.repository.ChatDataRepository
import com.freetalk.presenter.form.ChatRoomLeaveForm
import javax.inject.Inject

class LeaveChatRoomUseCase @Inject constructor(private val chatDataRepository: ChatDataRepository) {
    suspend operator fun invoke(chatRoomLeaveForm: ChatRoomLeaveForm): ChatRoomLeaveEntity {
        return chatDataRepository.leaveChatRoom(chatRoomLeaveForm = chatRoomLeaveForm)
    }
}