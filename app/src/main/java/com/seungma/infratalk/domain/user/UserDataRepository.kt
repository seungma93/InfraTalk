package com.seungma.infratalk.domain.user

import com.seungma.infratalk.presenter.sign.form.LoginForm
import com.seungma.infratalk.presenter.sign.form.ResetPasswordForm
import com.seungma.infratalk.presenter.sign.form.SignUpForm
import com.seungma.infratalk.presenter.sign.form.UserInfoUpdateForm

interface UserDataRepository {
    suspend fun signUp(signUpForm: SignUpForm): UserEntity
    suspend fun logIn(logInForm: LoginForm): UserEntity
    suspend fun resetPassword(resetPasswordForm: ResetPasswordForm): UserEntity
    suspend fun sendVerifiedEmail(): UserEntity
    suspend fun updateUserInfo(userInfoUpdateForm: UserInfoUpdateForm): UserEntity
    suspend fun deleteUserInfo(signUpForm: SignUpForm): UserEntity
    fun getUserInfo(): UserEntity
}
