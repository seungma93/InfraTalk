package com.sm.infratalk.domain.chat.usecase

import com.sm.infratalk.domain.chat.entity.ChatMessageSendEntity
import com.sm.infratalk.domain.chat.repository.ChatDataRepository
import com.sm.infratalk.presenter.chat.form.ChatMessageSendForm
import javax.inject.Inject

class SendChatMessageUseCase @Inject constructor(private val repository: ChatDataRepository) {
    suspend operator fun invoke(chatMessageSendForm: ChatMessageSendForm): ChatMessageSendEntity {
        return repository.sendChatMessage(chatMessageSendForm)
    }
}