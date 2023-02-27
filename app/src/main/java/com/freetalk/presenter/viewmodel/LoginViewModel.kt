package com.freetalk.presenter.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.freetalk.data.entity.UserEntity
import com.freetalk.data.remote.AuthData
import com.freetalk.usecase.UserUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

sealed class ViewEvent{
    data class SignUp(val authData: AuthData): ViewEvent()
    data class LogIn(val authData: AuthData): ViewEvent()
    data class ResetPassword(val authData: AuthData): ViewEvent()
}

class LoginViewModel(private val useCase: UserUseCase): ViewModel() {
    private val _viewEvent = MutableSharedFlow<ViewEvent>()
    val viewEvent: SharedFlow<ViewEvent> = _viewEvent.asSharedFlow()

    suspend fun signUp(userData: UserEntity) {
        kotlin.runCatching {
            _viewEvent.emit(ViewEvent.SignUp(useCase.signUp(userData)))
            //_viewEvent.emit(useCase.signUp(userData))
        }.onSuccess {
            Log.v("LoginVieModel", "성공")
        }.onFailure {
            Log.v("LoginVieModel", "에러 캐치")
        }
    }

    suspend fun logIn(userData: UserEntity) {
        kotlin.runCatching {
            _viewEvent.emit(ViewEvent.LogIn(useCase.logIn(userData)))
        }
    }

    suspend fun resetPassword(userData: UserEntity) {
        _viewEvent.emit(ViewEvent.ResetPassword(useCase.resetPassword(userData)))
    }
}