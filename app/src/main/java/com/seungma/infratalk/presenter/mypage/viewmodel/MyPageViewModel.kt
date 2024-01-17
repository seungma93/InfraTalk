package com.seungma.infratalk.presenter.mypage.viewmodel

import androidx.lifecycle.ViewModel
import com.seungma.infratalk.domain.mypage.usecase.UpdateUserInfoUseCase
import com.seungma.infratalk.domain.user.GetUserInfoUseCase
import com.seungma.infratalk.domain.user.UserEntity
import com.seungma.infratalk.presenter.sign.form.UserInfoUpdateForm
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
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

            com.seungma.infratalk.data.UserSingleton.userEntity = userEntity

            _viewEvent.emit(
                MyPageViewEvent.UpdateUserInfo(
                    userInfoUpdate = UserInfoUpdate(isSuccess = true)
                )
            )
        }.onFailure {

        }
    }

}