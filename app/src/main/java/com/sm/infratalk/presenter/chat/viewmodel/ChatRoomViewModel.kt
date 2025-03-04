package com.sm.infratalk.presenter.chat.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sm.infratalk.data.FailGetUserMeException
import com.sm.infratalk.data.FailSelectException
import com.sm.infratalk.domain.chat.entity.ChatRoomEntity
import com.sm.infratalk.domain.chat.entity.ChatRoomListEntity
import com.sm.infratalk.domain.chat.entity.ChatStartEntity
import com.sm.infratalk.domain.chat.usecase.LoadChatRoomListUseCase
import com.sm.infratalk.domain.chat.usecase.LoadRealTimeChatRoomListUseCase
import com.sm.infratalk.domain.user.entity.UserEntity
import com.sm.infratalk.domain.user.usecase.GetUserMeUseCase
import kotlinx.coroutines.CoroutineExceptionHandler
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
    private val getUserMeUseCase: GetUserMeUseCase,
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
        viewModelScope.launch(CoroutineExceptionHandler { _, exception ->
            // 예외 처리 로직
            when (exception) {
                is FailSelectException -> {
                    Log.d("seungma", "채팅방 프레그먼트 ")
                    // 추가적인 에러 처리 로직 (예: 에러 메시지 표시, 화면 전환 등)
                }
                else -> {
                    // 다른 예외 처리 로직
                    Log.e("seungma", "예외 발생: ", exception)
                    // 예외 처리 로직 (예: 에러 메시지 표시, 앱 종료 등)
                }
            }
        }) {
            runCatching {
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
            }.onFailure {
                throw FailSelectException("채팅방 로드 실패", it)
            }.getOrThrow()

        }
    }

    suspend fun loadChatRoom(): ChatRoomViewState {
        val result = kotlin.runCatching {
            loadChatRoomListUseCase()
        }.onFailure {
            throw FailSelectException("채팅방 로드 실패", it)
        }.getOrNull()

        return result?.let {
            _viewState.updateAndGet { _ ->
                Log.d("seungma", "loadChatRoom" + it.chatRoomList.size)
                viewState.value.copy(chatRoomListEntity = it)
            }
        } ?: viewState.value
    }

    suspend fun getUserMe(): UserEntity {
        return runCatching {
            getUserMeUseCase()
        }.onFailure {
            throw FailGetUserMeException("유저 정보 가져오기 실패")
        }.getOrThrow()
    }
}
