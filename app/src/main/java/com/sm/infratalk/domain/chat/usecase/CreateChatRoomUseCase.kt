package com.sm.infratalk.domain.chat.usecase

import com.sm.infratalk.domain.chat.entity.ChatRoomCreateEntity
import com.sm.infratalk.domain.chat.repository.ChatDataRepository
import com.sm.infratalk.presenter.chat.form.ChatRoomCreateForm
import javax.inject.Inject


class CreateChatRoomUseCase @Inject constructor(private val repository: ChatDataRepository) {
    suspend operator fun invoke(chatRoomCreateForm: ChatRoomCreateForm): ChatRoomCreateEntity {

        return repository.createChatRoom(chatRoomCreateForm = chatRoomCreateForm)
    }
}