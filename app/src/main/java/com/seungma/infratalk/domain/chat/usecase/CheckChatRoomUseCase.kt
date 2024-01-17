package com.seungma.infratalk.domain.chat.usecase

import com.seungma.infratalk.domain.chat.entity.ChatRoomCheckEntity
import com.seungma.infratalk.domain.chat.repository.ChatDataRepository
import com.seungma.infratalk.presenter.chat.form.ChatRoomCheckForm
import javax.inject.Inject


class CheckChatRoomUseCase @Inject constructor(private val repository: ChatDataRepository) {
    suspend operator fun invoke(chatRoomCheckForm: ChatRoomCheckForm): ChatRoomCheckEntity {

        return repository.checkChatRoom(chatRoomCheckForm = chatRoomCheckForm)
    }

}