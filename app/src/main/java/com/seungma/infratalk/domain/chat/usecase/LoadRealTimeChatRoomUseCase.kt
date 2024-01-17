package com.seungma.infratalk.domain.chat.usecase

import com.seungma.infratalk.domain.chat.entity.ChatRoomEntity
import com.seungma.infratalk.domain.chat.repository.ChatDataRepository
import com.seungma.infratalk.presenter.chat.form.RealTimeChatRoomLoadForm
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LoadRealTimeChatRoomUseCase @Inject constructor(
    private val chatDataRepository: ChatDataRepository
) {
    operator fun invoke(realTimeChatRoomLoadForm: RealTimeChatRoomLoadForm): Flow<ChatRoomEntity> {
        return chatDataRepository.loadRealTimeChatRoom(realTimeChatRoomLoadForm = realTimeChatRoomLoadForm)
    }
}