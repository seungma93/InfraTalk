package com.freetalk.presenter.viewmodel

import androidx.lifecycle.ViewModel
import com.freetalk.data.entity.UserEntity
import com.freetalk.data.remote.ImagesRequest
import com.freetalk.data.remote.SignUpForm
import com.freetalk.data.remote.UpdateForm
import com.freetalk.usecase.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

sealed class ViewEvent {
    data class SignUp(val userEntity: UserEntity) : ViewEvent()
    data class LogIn(val userEntity: UserEntity) : ViewEvent()
    data class ResetPassword(val userEntity: UserEntity) : ViewEvent()
    data class Error(val errorCode: Throwable) : ViewEvent()
}

class SignViewModel(
    private val signUpUseCase: SignUpUseCase,
    private val sendEmailUseCase: SendEmailUseCase,
    private val updateProfileImageUseCase: UpdateProfileImageUseCase,
    private val logInUseCase: LogInUseCase,
    private val resetPasswordUseCase: ResetPasswordUseCase
) : ViewModel() {
    private val _viewEvent = MutableSharedFlow<ViewEvent>()
    val viewEvent: SharedFlow<ViewEvent> = _viewEvent.asSharedFlow()

    suspend fun signUp(signUpForm: SignUpForm, imagesRequest: ImagesRequest?) {
        kotlin.runCatching {
            val signUpResult = signUpUseCase.signUp(signUpForm)
            val updateProfileResult = updateProfileImageUseCase.updateProfileImage(
                imagesRequest,
                UpdateForm(signUpResult.email, signUpForm.nickname, null)
            )
            sendEmailUseCase.sendVerifiedEmail()
            _viewEvent.emit(ViewEvent.SignUp(UserEntity(signUpResult.email, signUpResult.nickname, updateProfileResult.image)))
        }.onFailure {
            _viewEvent.emit(ViewEvent.Error(it))
        }
    }

    suspend fun logIn(userEntity: UserEntity) {
        kotlin.runCatching {
            _viewEvent.emit(ViewEvent.LogIn(logInUseCase.logIn(userEntity)))
        }
    }

    suspend fun resetPassword(userEntity: UserEntity) {
        _viewEvent.emit(ViewEvent.ResetPassword(resetPasswordUseCase.resetPassword(userEntity)))
    }
}