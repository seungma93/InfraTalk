package com.freetalk.domain.usecase

import com.freetalk.domain.entity.ChatMessageSendEntity
import com.freetalk.domain.repository.ChatDataRepository
import com.freetalk.presenter.form.ChatMessageSendForm
import javax.inject.Inject

class SendChatMessageUseCase @Inject constructor(private val repository: ChatDataRepository) {
    suspend operator fun invoke(chatMessageSendForm: ChatMessageSendForm): ChatMessageSendEntity {
        return repository.sendChatMessage(chatMessageSendForm)
    }
}