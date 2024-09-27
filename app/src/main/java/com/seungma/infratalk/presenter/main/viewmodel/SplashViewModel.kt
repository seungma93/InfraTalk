package com.seungma.infratalk.presenter.sign.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.seungma.infratalk.domain.user.entity.UserEntity
import com.seungma.infratalk.domain.user.usecase.GetUserMeUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject

sealed class SplashViewEvent {
    data class CheckLogin(val userEntity: UserEntity) : SplashViewEvent()
    data class Error(val errorCode: Throwable) : SplashViewEvent()
}

class SplashViewModel @Inject constructor(
    private val getUserMeUseCase: GetUserMeUseCase
) : ViewModel() {
    private val _viewEvent = MutableSharedFlow<SplashViewEvent>()
    val viewEvent: SharedFlow<SplashViewEvent> = _viewEvent.asSharedFlow()


    suspend fun checkLogin() {
        kotlin.runCatching {
            val userEntity = getUserMeUseCase()
            Log.d("SplashViewModel", "스플래시 뷰모델 :" + userEntity)
            _viewEvent.emit(SplashViewEvent.CheckLogin(userEntity = userEntity))
            Log.d("SplashViewModel", "스플래시 뷰모델 에밋 :")
        }.onFailure {
            Log.d("SplashViewModel", "스플래시 뷰모델 에러 :" + it.message)
            _viewEvent.emit(SplashViewEvent.Error(it))
        }
    }
}