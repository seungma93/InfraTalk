package com.freetalk.presenter.viewmodel

import android.util.Log
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.freetalk.domain.entity.BoardWriteEntity
import com.freetalk.domain.entity.ChatMessageListEntity
import com.freetalk.domain.entity.ChatMessageSend
import com.freetalk.domain.entity.ChatPrimaryKeyEntity
import com.freetalk.domain.entity.ChatRoomEntity
import com.freetalk.domain.entity.ChatRoomListEntity
import com.freetalk.domain.entity.ChatStartEntity
import com.freetalk.domain.entity.UserEntity
import com.freetalk.domain.usecase.CheckChatRoomUseCase
import com.freetalk.domain.usecase.GetUserInfoUseCase
import com.freetalk.domain.usecase.LoadChatMessageListUseCase
import com.freetalk.domain.usecase.LoadChatRoomListUseCase
import com.freetalk.domain.usecase.LoadRealTimeChatMessageUseCase
import com.freetalk.domain.usecase.LoadRealTimeChatRoomUseCase
import com.freetalk.domain.usecase.SendChatMessageUseCase
import com.freetalk.presenter.form.ChatMessageListLoadForm
import com.freetalk.presenter.form.ChatMessageSendForm
import com.freetalk.presenter.form.ChatRoomCheckForm
import com.freetalk.presenter.form.ChatRoomCreateForm
import com.freetalk.presenter.form.RealTimeChatMessageLoadForm
import com.freetalk.presenter.fragment.MainChildFragmentEndPoint
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.getAndUpdate
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.updateAndGet
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class ChatRoomViewEvent {
    data class ChatStart(val chatStartEntity: ChatStartEntity) : ChatRoomViewEvent()
    data class Error(val errorCode: Throwable) : ChatRoomViewEvent()
}

class ChatRoomViewModel @Inject constructor(
    private val loadChatRoomListUseCase: LoadChatRoomListUseCase,
    private val checkChatRoomUseCase: CheckChatRoomUseCase,
    private val getUserInfoUseCase: GetUserInfoUseCase,
    private val loadRealTimeChatRoomUseCase: LoadRealTimeChatRoomUseCase
) : ViewModel() {
    private val _viewEvent = MutableSharedFlow<ChatRoomViewEvent>()
    val viewEvent: SharedFlow<ChatRoomViewEvent> = _viewEvent.asSharedFlow()

    private val _viewState =
        MutableStateFlow(ChatRoomViewState(ChatRoomListEntity(emptyList())))
    val viewState: StateFlow<ChatRoomViewState> = _viewState
        .catch {
        }.stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            ChatRoomViewState(ChatRoomListEntity(emptyList()))
        )

    init {
        viewModelScope.launch {
            loadRealTimeChatRoomUseCase().collect { changedChatRoomListEntity ->
                _viewState.update {
                    val oldChatRoomList = it.chatRoomListEntity.chatRoomList

                    val filterRoomList = oldChatRoomList.filterNot { oldChatRoom ->
                        changedChatRoomListEntity.chatRoomList.any { changedChatRoom -> oldChatRoom.primaryKey == changedChatRoom.primaryKey }
                    }
                    val newChatRoomList =
                        (filterRoomList + changedChatRoomListEntity.chatRoomList).sortedByDescending { it.lastChatMessageEntity?.sendTime }


                    viewState.value.copy(
                        chatRoomListEntity = ChatRoomListEntity(
                            chatRoomList = newChatRoomList
                        )
                    )
                }
            }
        }
    }


    data class ChatRoomViewState(
        val chatRoomListEntity: ChatRoomListEntity,
    )

    suspend fun loadChatRoom(): ChatRoomViewState {
        val result = kotlin.runCatching {
            loadChatRoomListUseCase()
        }.onFailure {

        }.getOrNull()

        return result?.let {
            _viewState.updateAndGet { _ ->
                Log.d("seungma", "loadChatRoom" + it.chatRoomList.size)
                viewState.value.copy(chatRoomListEntity = it)
            }
        } ?: viewState.value
    }

    suspend fun startChat(chatRoomCheckForm: ChatRoomCheckForm) {
        kotlin.runCatching {
            val chatRoomCheckEntity =
                checkChatRoomUseCase(chatRoomCheckForm = chatRoomCheckForm)

            when (chatRoomCheckEntity.isChatRoom) {
                true -> {
                    _viewEvent.emit(
                        ChatRoomViewEvent.ChatStart(
                            chatStartEntity = ChatStartEntity(
                                chatPartner = chatRoomCheckForm.member[1],
                                chatRoomId = chatRoomCheckEntity.chatRoomId,
                                isSuccess = true
                            )
                        )
                    )
                }

                false -> {}
            }

        }.onFailure {
            _viewEvent.emit(ChatRoomViewEvent.Error(it))
        }
    }

    fun getUserInfo(): UserEntity {
        return getUserInfoUseCase()
    }
}
