package com.freetalk.domain.usecase

import com.freetalk.domain.entity.BoardEntity
import com.freetalk.domain.entity.BoardListEntity
import com.freetalk.domain.entity.ChatRoomCreateEntity
import com.freetalk.domain.repository.BookmarkDataRepository
import com.freetalk.domain.repository.ChatDataRepository
import com.freetalk.presenter.form.BoardBookmarkAddForm
import com.freetalk.presenter.form.ChatRoomCheckForm
import com.freetalk.presenter.form.ChatRoomCreateForm
import javax.inject.Inject


class CreateChatRoomUseCase @Inject constructor(private val repository: ChatDataRepository) {
    suspend operator fun invoke(
        chatRoomCreateForm: ChatRoomCreateForm, chatRoomCheckForm: ChatRoomCheckForm
    ): ChatRoomCreateEntity {
        val chatRoomCheckEntity = repository.checkChatRoom(chatRoomCheckForm = chatRoomCheckForm)
        return when (chatRoomCheckEntity.isChatRoom) {
            true -> ChatRoomCreateEntity(
                member = chatRoomCheckEntity.member,
                isSuccess = false
            )

            false -> repository.createChatRoom(chatRoomCreateForm = chatRoomCreateForm)
        }
    }
}