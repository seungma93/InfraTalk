package com.seungma.infratalk.domain.chat.usecase

import com.seungma.infratalk.domain.chat.entity.ChatMessageListEntity
import com.seungma.infratalk.domain.chat.repository.ChatDataRepository
import com.seungma.infratalk.presenter.chat.form.RealTimeChatMessageLoadForm
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LoadRealTimeChatMessageUseCase @Inject constructor(
    private val chatDataRepository: ChatDataRepository
) {
    operator fun invoke(realTimeChatMessageLoadForm: RealTimeChatMessageLoadForm): Flow<ChatMessageListEntity> {
        return chatDataRepository.loadRealTimeChatMessage(realTimeChatMessageLoadForm = realTimeChatMessageLoadForm)
    }
}