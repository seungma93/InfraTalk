package com.freetalk.presenter.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.freetalk.domain.entity.BoardListEntity
import com.freetalk.domain.entity.BoardWriteEntity
import com.freetalk.domain.entity.ChatMessageListEntity
import com.freetalk.domain.entity.ChatMessageSend
import com.freetalk.domain.entity.ChatRoomCheckEntity
import com.freetalk.domain.entity.ChatStartEntity
import com.freetalk.domain.entity.UserEntity
import com.freetalk.domain.usecase.AddBoardBookmarkUseCase
import com.freetalk.domain.usecase.AddBoardLikeUseCase
import com.freetalk.domain.usecase.CheckChatRoomUseCase
import com.freetalk.domain.usecase.CreateChatRoomUseCase
import com.freetalk.domain.usecase.DeleteBoardBookmarkUseCase
import com.freetalk.domain.usecase.DeleteBoardLikeUseCase
import com.freetalk.domain.usecase.GetUserInfoUseCase
import com.freetalk.domain.usecase.LoadBoardListUseCase
import com.freetalk.domain.usecase.LoadChatMessageListUseCase
import com.freetalk.domain.usecase.SendChatMessageUseCase
import com.freetalk.domain.usecase.UpdateBoardContentImagesUseCase
import com.freetalk.domain.usecase.WriteBoardContentUseCase
import com.freetalk.presenter.form.BoardBookmarkAddForm
import com.freetalk.presenter.form.BoardBookmarkDeleteForm
import com.freetalk.presenter.form.BoardContentImagesUpdateForm
import com.freetalk.presenter.form.BoardContentInsertForm
import com.freetalk.presenter.form.BoardLikeAddForm
import com.freetalk.presenter.form.BoardLikeCountLoadForm
import com.freetalk.presenter.form.BoardLikeDeleteForm
import com.freetalk.presenter.form.BoardListLoadForm
import com.freetalk.presenter.form.ChatMessageListLoadForm
import com.freetalk.presenter.form.ChatMessageSendForm
import com.freetalk.presenter.form.ChatRoomCheckForm
import com.freetalk.presenter.form.ChatRoomCreateForm
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.updateAndGet
import javax.inject.Inject

sealed class ChatViewEvent {
    data class SendMessage(val chatMessageSend: ChatMessageSend) : ChatViewEvent()
    data class Error(val errorCode: Throwable) : ChatViewEvent()
}

class ChatViewModel @Inject constructor(
    private val sendChatMessageUseCase: SendChatMessageUseCase,
    private val loadChatMessageListUseCase: LoadChatMessageListUseCase
) : ViewModel() {
    private val _viewEvent = MutableSharedFlow<ChatViewEvent>()
    val viewEvent: SharedFlow<ChatViewEvent> = _viewEvent.asSharedFlow()

    private val _viewState =
        MutableStateFlow(ChatViewState(ChatMessageListEntity(emptyList())))
    val viewState: StateFlow<ChatViewState> = _viewState.asStateFlow()

    data class ChatViewState(
        val chatMessageListEntity: ChatMessageListEntity
    )

    suspend fun sendChatMessage(
        chatMessageSendForm: ChatMessageSendForm
    ) {
        kotlin.runCatching {
            val chatMessageSendEntity =
                sendChatMessageUseCase(chatMessageSendForm = chatMessageSendForm)

            _viewEvent.emit(
                ChatViewEvent.SendMessage(
                    chatMessageSend = ChatMessageSend(
                        isSuccess = chatMessageSendEntity.isSuccess
                    )
                )
            )

        }.onFailure {
            _viewEvent.emit(ChatViewEvent.Error(it))
        }
    }

    suspend fun loadChatMessage(
        chatMessageListLoadForm: ChatMessageListLoadForm
    ): ChatViewState {
        val result = kotlin.runCatching {
            val chatMessageListEntity = loadChatMessageListUseCase(chatMessageListLoadForm = chatMessageListLoadForm)
            when (chatMessageListLoadForm.reload) {
                true -> chatMessageListEntity.chatMessageList
                false -> viewState.value.chatMessageListEntity.chatMessageList + chatMessageListEntity.chatMessageList
            }
        }.onFailure {

        }.getOrNull()

        return result?.let {
            _viewState.updateAndGet { _ ->
                viewState.value.copy(chatMessageListEntity = ChatMessageListEntity(it))
            }
        } ?: viewState.value
    }

}