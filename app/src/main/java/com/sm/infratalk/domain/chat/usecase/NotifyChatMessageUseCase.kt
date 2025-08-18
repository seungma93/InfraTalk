package com.sm.infratalk.domain.chat.usecase

import com.sm.infratalk.domain.chat.entity.ChatNotifyEntity
import com.sm.infratalk.domain.chat.repository.ChatDataRepository
import com.sm.infratalk.presenter.chat.form.ChatMessageNotifyForm
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class NotifyChatMessageUseCase @Inject constructor(
    private val chatDataRepository: ChatDataRepository
) {
    operator fun invoke(chatMessageNotifyForm: ChatMessageNotifyForm): Flow<ChatNotifyEntity> {
        return chatDataRepository.notifyChatMessage(chatMessageNotifyForm = chatMessageNotifyForm)
    }
} 