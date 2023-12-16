package com.freetalk.presenter.viewmodel

import android.util.Log
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.freetalk.data.UserSingleton
import com.freetalk.domain.entity.ChatMessageListEntity
import com.freetalk.domain.entity.ChatMessageSend
import com.freetalk.domain.entity.ChatPrimaryKeyEntity
import com.freetalk.domain.entity.ChatRoomEntity
import com.freetalk.domain.entity.ChatRoomLeave
import com.freetalk.domain.entity.UserEntity
import com.freetalk.domain.usecase.GetUserInfoUseCase
import com.freetalk.domain.usecase.LeaveChatRoomUseCase
import com.freetalk.domain.usecase.LoadChatMessageListUseCase
import com.freetalk.domain.usecase.LoadChatRoomUseCase
import com.freetalk.domain.usecase.LoadRealTimeChatMessageUseCase
import com.freetalk.domain.usecase.LoadRealTimeChatRoomListUseCase
import com.freetalk.domain.usecase.LoadRealTimeChatRoomUseCase
import com.freetalk.domain.usecase.SendChatMessageUseCase
import com.freetalk.domain.usecase.UpdateUserInfoUseCase
import com.freetalk.presenter.form.ChatMessageListLoadForm
import com.freetalk.presenter.form.ChatMessageSendForm
import com.freetalk.presenter.form.ChatRoomLeaveForm
import com.freetalk.presenter.form.ChatRoomLoadForm
import com.freetalk.presenter.form.RealTimeChatMessageLoadForm
import com.freetalk.presenter.form.RealTimeChatRoomLoadForm
import com.freetalk.presenter.form.UserInfoUpdateForm
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEmpty
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.updateAndGet
import kotlinx.coroutines.launch
import javax.inject.Inject


sealed class MyPageViewEvent {
    data class UpdateUserInfo(val userInfoUpdate: UserInfoUpdate) : MyPageViewEvent()
    data class Error(val errorCode: Throwable) : MyPageViewEvent()
}


data class UserInfoUpdate(
    val isSuccess: Boolean
)

class MyPageViewModel @Inject constructor(
    private val getUserInfoUseCase: GetUserInfoUseCase,
    private val updateUserInfoUseCase: UpdateUserInfoUseCase
) : ViewModel() {

    private val _viewEvent = MutableSharedFlow<MyPageViewEvent>()
    val viewEvent: SharedFlow<MyPageViewEvent> = _viewEvent.asSharedFlow()

    /*
        private val _viewState =
            MutableStateFlow(ChatViewState(ChatMessageListEntity(emptyList()), false, null))
        val viewState: StateFlow<ChatViewState> = _viewState
            .catch {
            }.stateIn(
                viewModelScope,
                SharingStarted.Eagerly,
                ChatViewState(ChatMessageListEntity(emptyList()), false, null)
            )

        data class ChatViewState(
            val chatMessageListEntity: ChatMessageListEntity,
            val isNewChatMessage: Boolean,
            val chatRoomEntity: ChatRoomEntity?
        )

         */

    fun getUserInfo(): UserEntity {
        return getUserInfoUseCase()
    }

    suspend fun updateUserInfo(userInfoUpdateForm: UserInfoUpdateForm) {
        kotlin.runCatching {
             val userEntity = updateUserInfoUseCase(userInfoUpdateForm = userInfoUpdateForm)

            UserSingleton.userEntity = userEntity

            _viewEvent.emit(
                MyPageViewEvent.UpdateUserInfo(
                    userInfoUpdate = UserInfoUpdate(isSuccess = true)
                )
            )
        }.onFailure {

        }
    }

}