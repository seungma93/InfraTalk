package com.freetalk.domain.usecase

import com.freetalk.domain.entity.ChatMessageListEntity
import com.freetalk.domain.repository.ChatDataRepository
import com.freetalk.presenter.form.RealTimeChatMessageLoadForm
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LoadRealTimeChatMessageUseCase @Inject constructor(
    private val chatDataRepository: ChatDataRepository
) {
    operator fun invoke(realTimeChatMessageLoadForm: RealTimeChatMessageLoadForm): Flow<ChatMessageListEntity> {
        return chatDataRepository.loadRealTimeChatMessage(realTimeChatMessageLoadForm = realTimeChatMessageLoadForm)
    }
}