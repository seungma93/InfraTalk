package com.sm.infratalk.domain.chat.usecase

import com.sm.infratalk.domain.chat.entity.ChatMessageListEntity
import com.sm.infratalk.domain.chat.repository.ChatDataRepository
import com.sm.infratalk.presenter.chat.form.ChatMessageListLoadForm
import javax.inject.Inject

class LoadChatMessageListUseCase @Inject constructor(private val chatDataRepository: ChatDataRepository) {
    suspend operator fun invoke(chatMessageListLoadForm: ChatMessageListLoadForm): ChatMessageListEntity {
        return chatDataRepository.loadChatMessageList(chatMessageListLoadForm = chatMessageListLoadForm)
    }
}