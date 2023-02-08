package com.freetalk.presenter.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.freetalk.data.entity.UserEntity
import com.freetalk.data.remote.AuthData
import com.freetalk.usecase.UserUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class LoginViewModel(private val useCase: UserUseCase): ViewModel() {
    private val _signUpEvent = MutableSharedFlow<AuthData>()
    val signUpEvent: SharedFlow<AuthData> = _signUpEvent.asSharedFlow()
    private val _logInEvent = MutableSharedFlow<AuthData>()
    val logInEvent: SharedFlow<AuthData> = _logInEvent.asSharedFlow()
    private val _resetPasswordEvent = MutableSharedFlow<AuthData>()
    val resetPasswordEvent: SharedFlow<AuthData> = _resetPasswordEvent.asSharedFlow()

    suspend fun signUp(userData: UserEntity) {
        kotlin.runCatching {
            _signUpEvent.emit(useCase.signUp(userData))
        }.onSuccess {
            Log.v("LoginVieModel", "성공")
        }.onFailure {
            Log.v("LoginVieModel", "에러 캐치")
        }
    }

    suspend fun logIn(userData: UserEntity) {
        kotlin.runCatching {
            _logInEvent.emit(useCase.logIn(userData))
        }
    }

    suspend fun resetPassword(userData: UserEntity) {
        _resetPasswordEvent.emit(useCase.resetPassword(userData))
    }
}