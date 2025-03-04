package com.sm.infratalk.presenter.mypage.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.sm.infratalk.data.FailGetUserMeException
import com.sm.infratalk.domain.login.usecase.LogoutUseCase
import com.sm.infratalk.domain.mypage.usecase.UpdateUserInfoUseCase
import com.sm.infratalk.domain.user.entity.UserEntity
import com.sm.infratalk.domain.user.usecase.GetUserMeUseCase
import com.sm.infratalk.presenter.sign.form.UserInfoUpdateForm
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject


sealed class MyPageViewEvent {
    data class UpdateUserInfo(val userInfoUpdate: UserInfoUpdate) : MyPageViewEvent()
    data class logout(val isSuccess: Boolean) : MyPageViewEvent()
    data class Error(val errorCode: Throwable) : MyPageViewEvent()
}


data class UserInfoUpdate(
    val isSuccess: Boolean
)

class MyPageViewModel @Inject constructor(
    private val getUserMeUseCase: GetUserMeUseCase,
    private val updateUserInfoUseCase: UpdateUserInfoUseCase,
    private val logoutUseCase: LogoutUseCase
) : ViewModel() {

    private val _viewEvent = MutableSharedFlow<MyPageViewEvent>()
    val viewEvent: SharedFlow<MyPageViewEvent> = _viewEvent.asSharedFlow()


    suspend fun getUserMe(): UserEntity {
        return runCatching {
            getUserMeUseCase()
        }.onFailure {
            throw FailGetUserMeException("유저 정보 가져오기 실패")
        }.getOrThrow()
    }

    suspend fun updateUserInfo(userInfoUpdateForm: UserInfoUpdateForm) {
        kotlin.runCatching {
            updateUserInfoUseCase(userInfoUpdateForm = userInfoUpdateForm)

            _viewEvent.emit(
                MyPageViewEvent.UpdateUserInfo(
                    userInfoUpdate = UserInfoUpdate(isSuccess = true)
                )
            )
        }.onFailure {
            Log.d("seungma", " 마이페이지 뷰모델 온페일러")
        }
    }

    suspend fun logout() {
        runCatching {
            logoutUseCase()
            _viewEvent.emit(
                MyPageViewEvent.logout(
                    isSuccess = true
                )
            )
        }.onFailure {
            _viewEvent.emit(
                MyPageViewEvent.logout(
                    isSuccess = false
                )
            )
        }
    }

}