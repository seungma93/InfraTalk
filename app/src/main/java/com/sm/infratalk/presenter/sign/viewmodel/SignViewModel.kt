package com.sm.infratalk.presenter.sign.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sm.infratalk.data.model.request.image.ImagesRequest
import com.sm.infratalk.domain.login.usecase.LoginUseCase
import com.sm.infratalk.domain.login.usecase.ResetPasswordUseCase
import com.sm.infratalk.domain.mypage.usecase.UpdateUserInfoUseCase
import com.sm.infratalk.domain.signup.usecase.DeleteUserInfoUseCase
import com.sm.infratalk.domain.signup.usecase.SendEmailUseCase
import com.sm.infratalk.domain.signup.usecase.SignUpUseCase
import com.sm.infratalk.domain.user.entity.SavedEmailGetEntity
import com.sm.infratalk.domain.user.entity.UserEntity
import com.sm.infratalk.domain.user.usecase.DeleteSavedEmailUseCase
import com.sm.infratalk.domain.user.usecase.GetSavedEmailUseCase
import com.sm.infratalk.domain.user.usecase.SetSavedEmailUseCase
import com.sm.infratalk.presenter.sign.form.LoginForm
import com.sm.infratalk.presenter.sign.form.ResetPasswordForm
import com.sm.infratalk.presenter.sign.form.SavedEmailSetForm
import com.sm.infratalk.presenter.sign.form.SignUpForm
import com.sm.infratalk.presenter.sign.form.UserInfoUpdateForm
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class ViewEvent {
    data class SignUp(val userEntity: UserEntity) : ViewEvent()
    data class LogIn(val userEntity: UserEntity) : ViewEvent()
    data class ResetPassword(val userEntity: UserEntity) : ViewEvent()
    data class Error(val throwable: Throwable) : ViewEvent()
}

class SignViewModel @Inject constructor(
    private val signUpUseCase: SignUpUseCase,
    private val sendEmailUseCase: SendEmailUseCase,
    private val logInUseCase: LoginUseCase,
    private val resetPasswordUseCase: ResetPasswordUseCase,
    private val deleteUserInfoUseCase: DeleteUserInfoUseCase,
    private val updateUserInfoUseCase: UpdateUserInfoUseCase,
    private val setSavedEmailUseCase: SetSavedEmailUseCase,
    private val getSavedEmailUseCase: GetSavedEmailUseCase,
    private val deleteSavedEmailUseCase: DeleteSavedEmailUseCase
) : ViewModel() {
    private val _viewEvent = MutableSharedFlow<ViewEvent>()
    val viewEvent: SharedFlow<ViewEvent> = _viewEvent.asSharedFlow()

    suspend fun signUp(signUpForm: SignUpForm, imagesRequest: ImagesRequest?) {
        kotlin.runCatching {
            val signUpResult = signUpUseCase(signUpForm)
            val updateUserEntity = updateUserInfoUseCase(
                userInfoUpdateForm = UserInfoUpdateForm(
                    email = signUpResult.email,
                    nickname = signUpResult.nickname,
                    image = imagesRequest?.imageUris?.first()
                )
            )
            sendEmailUseCase()

            _viewEvent.emit(
                ViewEvent.SignUp(
                    UserEntity(
                        signUpResult.email,
                        signUpResult.nickname,
                        updateUserEntity.image
                    )
                )
            )
        }.onFailure {
            deleteUserInfoUseCase(signUpForm)
            _viewEvent.emit(ViewEvent.Error(throwable = it))
        }
    }

    suspend fun logIn(loginForm: LoginForm) {
        kotlin.runCatching {
            Log.d("SignViewModel", "로그인 뷰 모델")
            _viewEvent.emit(ViewEvent.LogIn(logInUseCase(loginForm)))
        }.onFailure {
            _viewEvent.emit(ViewEvent.Error(throwable = it))
        }
    }

    suspend fun resetPassword(resetPasswordForm: ResetPasswordForm) {
        kotlin.runCatching {
            _viewEvent.emit(
                ViewEvent.ResetPassword(
                    resetPasswordUseCase(
                        resetPasswordForm
                    )
                )
            )
        }.onFailure {
            _viewEvent.emit(ViewEvent.Error(throwable = it))
        }
    }

    fun setSavedEmail(savedEmailSetForm: SavedEmailSetForm) {
        runCatching {
            setSavedEmailUseCase(savedEmailSetForm = savedEmailSetForm)
        }.onFailure {
            viewModelScope.launch {
                _viewEvent.emit(ViewEvent.Error(throwable = it))
            }
        }
    }

    fun getSavedEmail(): SavedEmailGetEntity {
        return getSavedEmailUseCase()
    }

    fun deleteSavedEmail() {
        runCatching {
            deleteSavedEmailUseCase()
        }.onFailure {
            viewModelScope.launch {
                _viewEvent.emit(ViewEvent.Error(throwable = it))
            }
        }
    }
}