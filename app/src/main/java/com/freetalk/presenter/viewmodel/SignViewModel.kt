package com.freetalk.presenter.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.freetalk.data.model.request.ImagesRequest
import com.freetalk.domain.entity.UserEntity
import com.freetalk.domain.usecase.DeleteUserInfoUseCase
import com.freetalk.domain.usecase.LogInUseCase
import com.freetalk.domain.usecase.ResetPasswordUseCase
import com.freetalk.domain.usecase.SendEmailUseCase
import com.freetalk.domain.usecase.SignUpUseCase
import com.freetalk.domain.usecase.UpdateProfileImageUseCase
import com.freetalk.presenter.form.LogInForm
import com.freetalk.presenter.form.ResetPasswordForm
import com.freetalk.presenter.form.SignUpForm
import com.freetalk.presenter.form.UpdateForm
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
            val updateProfileResult = updateProfileImageUseCase.updateProfileImage(
                imagesRequest,
                UpdateForm(signUpResult.email, signUpForm.nickname, null)
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