package com.sm.infratalk.domain.chat.usecase

import com.sm.infratalk.domain.chat.entity.ChatRoomCheckEntity
import com.sm.infratalk.domain.chat.repository.ChatDataRepository
import com.sm.infratalk.presenter.chat.form.ChatRoomCheckForm
import javax.inject.Inject


class CheckChatRoomUseCase @Inject constructor(private val repository: ChatDataRepository) {
    suspend operator fun invoke(chatRoomCheckForm: ChatRoomCheckForm): ChatRoomCheckEntity {

        return repository.checkChatRoom(chatRoomCheckForm = chatRoomCheckForm)
    }

}