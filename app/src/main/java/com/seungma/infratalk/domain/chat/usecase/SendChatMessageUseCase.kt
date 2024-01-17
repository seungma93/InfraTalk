package com.seungma.infratalk.domain.chat.usecase

import com.seungma.infratalk.domain.chat.entity.ChatMessageSendEntity
import com.seungma.infratalk.domain.chat.repository.ChatDataRepository
import com.seungma.infratalk.presenter.chat.form.ChatMessageSendForm
import javax.inject.Inject

class SendChatMessageUseCase @Inject constructor(private val repository: ChatDataRepository) {
    suspend operator fun invoke(chatMessageSendForm: ChatMessageSendForm): ChatMessageSendEntity {
        return repository.sendChatMessage(chatMessageSendForm)
    }
}