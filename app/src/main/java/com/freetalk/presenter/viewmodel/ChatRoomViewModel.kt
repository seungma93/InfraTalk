package com.freetalk.presenter.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.freetalk.domain.entity.ChatRoomEntity
import com.freetalk.domain.entity.ChatRoomListEntity
import com.freetalk.domain.entity.ChatStartEntity
import com.freetalk.domain.entity.UserEntity
import com.freetalk.domain.usecase.CheckChatRoomUseCase
import com.freetalk.domain.usecase.GetUserInfoUseCase
import com.freetalk.domain.usecase.LoadChatRoomListUseCase
import com.freetalk.domain.usecase.LoadRealTimeChatRoomListUseCase
import com.freetalk.presenter.form.ChatRoomCheckForm
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
import java.util.Date
import javax.inject.Inject

sealed class ChatRoomViewEvent {
    data class ChatStart(val chatStartEntity: ChatStartEntity) : ChatRoomViewEvent()
    data class Error(val errorCode: Throwable) : ChatRoomViewEvent()
}

data class ChatRoomViewState(
    val chatRoomListEntity: ChatRoomListEntity
)

class ChatRoomViewModel @Inject constructor(
    private val loadChatRoomListUseCase: LoadChatRoomListUseCase,
    private val getUserInfoUseCase: GetUserInfoUseCase,
    private val loadRealTimeChatRoomListUseCase: LoadRealTimeChatRoomListUseCase
) : ViewModel() {
    private val _viewEvent = MutableSharedFlow<ChatRoomViewEvent>()
    val viewEvent: SharedFlow<ChatRoomViewEvent> = _viewEvent.asSharedFlow()

    private val initChatRoomListEntity = ChatRoomListEntity(
        chatRoomList = listOf(
            ChatRoomEntity(
                primaryKey = "",
                roomName = "",
                roomThumbnail = null,
                createTime = Date(System.currentTimeMillis()),
                member = emptyList(),
                leaveMember = emptyList(),
                lastChatMessageEntity = null
            )
        )
    )

    private val _viewState =
        MutableStateFlow(ChatRoomViewState(initChatRoomListEntity))
    val viewState: StateFlow<ChatRoomViewState> = _viewState
        .catch {
        }.stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            ChatRoomViewState(initChatRoomListEntity)
        )

    init {
        Log.d("seungma", "init")
        viewModelScope.launch {
            loadRealTimeChatRoomListUseCase().collect { changedChatRoomListEntity ->
                Log.d("seungma", "뷰모델" + changedChatRoomListEntity.chatRoomList.size)
                _viewState.update {
                    val oldChatRoomList = it.chatRoomListEntity.chatRoomList

                    val filterRoomList = oldChatRoomList.filterNot { oldChatRoom ->
                        changedChatRoomListEntity.chatRoomList.any { changedChatRoom -> oldChatRoom.primaryKey == changedChatRoom.primaryKey }
                    }
                    val newChatRoomList =
                        (filterRoomList + changedChatRoomListEntity.chatRoomList).sortedWith(
                            compareByDescending { chatRoom ->
                                chatRoom.lastChatMessageEntity?.sendTime ?: chatRoom.createTime
                            }
                        )
                    viewState.value.copy(
                        chatRoomListEntity = ChatRoomListEntity(
                            chatRoomList = newChatRoomList
                        )
                    )
                }
            }
        }
    }

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

    fun getUserInfo(): UserEntity {
        return getUserInfoUseCase()
    }
}
