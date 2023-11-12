package com.freetalk.domain.usecase

import com.freetalk.domain.entity.ChatRoomEntity
import com.freetalk.domain.entity.ChatRoomListEntity
import com.freetalk.domain.repository.ChatDataRepository
import com.freetalk.presenter.form.RealTimeChatRoomLoadForm
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LoadRealTimeChatRoomUseCase @Inject constructor(
    private val chatDataRepository: ChatDataRepository
) {
    operator fun invoke(realTimeChatRoomLoadForm: RealTimeChatRoomLoadForm): Flow<ChatRoomEntity> {
        return chatDataRepository.loadRealTimeChatRoom(realTimeChatRoomLoadForm = realTimeChatRoomLoadForm)
    }
}