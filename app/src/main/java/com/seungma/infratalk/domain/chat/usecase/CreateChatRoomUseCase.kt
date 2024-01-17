package com.seungma.infratalk.domain.chat.usecase

import com.seungma.infratalk.domain.chat.entity.ChatRoomCreateEntity
import com.seungma.infratalk.domain.chat.repository.ChatDataRepository
import com.seungma.infratalk.presenter.chat.form.ChatRoomCreateForm
import javax.inject.Inject


class CreateChatRoomUseCase @Inject constructor(private val repository: ChatDataRepository) {
    suspend operator fun invoke(chatRoomCreateForm: ChatRoomCreateForm): ChatRoomCreateEntity {

        return repository.createChatRoom(chatRoomCreateForm = chatRoomCreateForm)
    }
}