package com.freetalk.domain.usecase

import com.freetalk.domain.entity.ChatMessageListEntity
import com.freetalk.domain.repository.ChatDataRepository
import com.freetalk.presenter.form.ChatMessageListLoadForm
import javax.inject.Inject

class LoadChatMessageListUseCase @Inject constructor(private val chatDataRepository: ChatDataRepository) {
    suspend operator fun invoke(chatMessageListLoadForm: ChatMessageListLoadForm): ChatMessageListEntity {
        return chatDataRepository.loadChatMessageList(chatMessageListLoadForm = chatMessageListLoadForm)
    }
}