package com.seungma.infratalk.presenter.sign.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.seungma.infratalk.data.model.request.image.ImagesRequest
import com.seungma.infratalk.domain.login.usecase.LogInUseCase
import com.seungma.infratalk.domain.login.usecase.ResetPasswordUseCase
import com.seungma.infratalk.domain.mypage.usecase.UpdateProfileImageUseCase
import com.seungma.infratalk.domain.signup.usecase.DeleteUserInfoUseCase
import com.seungma.infratalk.domain.signup.usecase.SendEmailUseCase
import com.seungma.infratalk.domain.signup.usecase.SignUpUseCase
import com.seungma.infratalk.domain.user.UserEntity
import com.seungma.infratalk.presenter.sign.form.LogInForm
import com.seungma.infratalk.presenter.sign.form.ResetPasswordForm
import com.seungma.infratalk.presenter.sign.form.SignUpForm
import com.seungma.infratalk.presenter.sign.form.UserInfoUpdateForm
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject

sealed class ViewEvent {
    data class SignUp(val userEntity: UserEntity) : ViewEvent()
    data class LogIn(val userEntity: UserEntity) : ViewEvent()
    data class ResetPassword(val userEntity: UserEntity) : ViewEvent()
    data class Error(val errorCode: Throwable) : ViewEvent()
}

class SignViewModel @Inject constructor(
    private val signUpUseCase: SignUpUseCase,
    private val sendEmailUseCase: SendEmailUseCase,
    private val updateProfileImageUseCase: UpdateProfileImageUseCase,
    private val logInUseCase: LogInUseCase,
    private val resetPasswordUseCase: ResetPasswordUseCase,
    private val deleteUserInfoUseCase: DeleteUserInfoUseCase
) : ViewModel() {
    private val _viewEvent = MutableSharedFlow<ViewEvent>()
    val viewEvent: SharedFlow<ViewEvent> = _viewEvent.asSharedFlow()

    suspend fun signUp(signUpForm: SignUpForm, imagesRequest: ImagesRequest?) {
        kotlin.runCatching {
            val signUpResult = signUpUseCase.signUp(signUpForm)
            val updateProfileResult = updateProfileImageUseCase(
                imagesRequest,
                UserInfoUpdateForm(signUpResult.email, signUpForm.nickname, null)
            )

            sendEmailUseCase.sendVerifiedEmail()

            _viewEvent.emit(
                ViewEvent.SignUp(
                    UserEntity(
                        signUpResult.email,
                        signUpResult.nickname,
                        updateProfileResult.image
                    )
                )
            )
        }.onFailure {
            deleteUserInfoUseCase.deleteUserInfo(signUpForm)
            _viewEvent.emit(ViewEvent.Error(it))
        }
    }

    suspend fun logIn(logInForm: LogInForm) {
        kotlin.runCatching {
            Log.d("SignViewModel", "로그인 뷰 모델")
            _viewEvent.emit(ViewEvent.LogIn(logInUseCase.logIn(logInForm)))
        }.onFailure {
            _viewEvent.emit(ViewEvent.Error(it))
        }
    }

    suspend fun resetPassword(resetPasswordForm: ResetPasswordForm) {
        kotlin.runCatching {
            _viewEvent.emit(
                ViewEvent.ResetPassword(
                    resetPasswordUseCase.resetPassword(
                        resetPasswordForm
                    )
                )
            )
        }.onFailure {
            _viewEvent.emit(ViewEvent.Error(it))
        }
    }
}