package com.freetalk.presenter.viewmodel

import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.freetalk.domain.entity.ChatMessageListEntity
import com.freetalk.domain.entity.ChatMessageSend
import com.freetalk.domain.entity.ChatPrimaryKeyEntity
import com.freetalk.domain.entity.ChatRoomEntity
import com.freetalk.domain.usecase.LoadChatMessageListUseCase
import com.freetalk.domain.usecase.LoadChatRoomUseCase
import com.freetalk.domain.usecase.LoadRealTimeChatMessageUseCase
import com.freetalk.domain.usecase.SendChatMessageUseCase
import com.freetalk.presenter.form.ChatMessageListLoadForm
import com.freetalk.presenter.form.ChatMessageSendForm
import com.freetalk.presenter.form.ChatRoomLoadForm
import com.freetalk.presenter.form.RealTimeChatMessageLoadForm
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.updateAndGet
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class ChatViewEvent {
    data class SendMessage(val chatMessageSend: ChatMessageSend) : ChatViewEvent()
    data class Error(val errorCode: Throwable) : ChatViewEvent()
}

class ChatViewModelFactory @Inject constructor(
    private val sendChatMessageUseCase: SendChatMessageUseCase,
    private val loadChatMessageListUseCase: LoadChatMessageListUseCase,
    private val loadRealTimeChatMessageUseCase: LoadRealTimeChatMessageUseCase,
    private val loadChatRoomUseCase: LoadChatRoomUseCase
) : AbstractSavedStateViewModelFactory() {
    override fun <T : ViewModel> create(
        key: String,
        modelClass: Class<T>,
        handle: SavedStateHandle
    ): T {
        @Suppress("UNCHECKED_CAST")
        return ChatViewModel(
            handle,
            sendChatMessageUseCase,
            loadChatMessageListUseCase,
            loadRealTimeChatMessageUseCase,
            loadChatRoomUseCase
        ) as T
    }
}

class ChatViewModel @Inject constructor(
    private val stateHandle: SavedStateHandle,
    private val sendChatMessageUseCase: SendChatMessageUseCase,
    private val loadChatMessageListUseCase: LoadChatMessageListUseCase,
    private val loadRealTimeChatMessageUseCase: LoadRealTimeChatMessageUseCase,
    private val loadChatRoomUseCase: LoadChatRoomUseCase
) : ViewModel() {
    private val _viewEvent = MutableSharedFlow<ChatViewEvent>()
    val viewEvent: SharedFlow<ChatViewEvent> = _viewEvent.asSharedFlow()

    private val chatEntity = requireNotNull(
        stateHandle.get("CHAT_PRIMARY_KEY") as? ChatPrimaryKeyEntity
    )

    private val _viewState =
        MutableStateFlow(ChatViewState(ChatMessageListEntity(emptyList()), false, null))
    val viewState: StateFlow<ChatViewState> = _viewState
        .catch {
        }.stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            ChatViewState(ChatMessageListEntity(emptyList()), false, null)
        )

    init {
        viewModelScope.launch {
            loadRealTimeChatMessageUseCase(
                RealTimeChatMessageLoadForm(
                    chatRoomId = chatEntity.chatRoomId
                )
            ).collect { newChat ->
                _viewState.update {
                    val newChatList =
                        newChat.chatMessageList + it.chatMessageListEntity.chatMessageList
                    viewState.value.copy(chatMessageListEntity = ChatMessageListEntity(chatMessageList = newChatList), isNewChatMessage = true)
                }
            }
        }
    }

    data class ChatViewState(
        val chatMessageListEntity: ChatMessageListEntity,
        val isNewChatMessage: Boolean,
        val chatRoomEntity: ChatRoomEntity?
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
            val chatMessageListEntity =
                loadChatMessageListUseCase(chatMessageListLoadForm = chatMessageListLoadForm)
            when (chatMessageListLoadForm.reload) {
                true -> chatMessageListEntity.chatMessageList
                false -> viewState.value.chatMessageListEntity.chatMessageList + chatMessageListEntity.chatMessageList
            }
        }.onFailure {

        }.getOrNull()

        return result?.let {
            _viewState.updateAndGet { _ ->
                viewState.value.copy(chatMessageListEntity = ChatMessageListEntity(it), isNewChatMessage = chatMessageListLoadForm.reload)
            }
        } ?: viewState.value
    }

    suspend fun loadChatRoomName(
        chatRoomLoadForm: ChatRoomLoadForm
    ): ChatViewState {
        val result = kotlin.runCatching {
                loadChatRoomUseCase(chatRoomLoadForm = chatRoomLoadForm)

        }.onFailure {

        }.getOrNull()

        return result?.let {
            _viewState.updateAndGet { _ ->
                viewState.value.copy(chatRoomEntity = it)
            }
        } ?: viewState.value
    }


}