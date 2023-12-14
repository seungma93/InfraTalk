package com.freetalk.domain.repository

import com.freetalk.domain.entity.UserEntity
import com.freetalk.presenter.form.LogInForm
import com.freetalk.presenter.form.ResetPasswordForm
import com.freetalk.presenter.form.SignUpForm
import com.freetalk.presenter.form.UserInfoUpdateForm

interface UserDataRepository {
    suspend fun signUp(signUpForm: SignUpForm): UserEntity
    suspend fun logIn(logInForm: LogInForm): UserEntity
    suspend fun resetPassword(resetPasswordForm: ResetPasswordForm): UserEntity
    suspend fun sendVerifiedEmail(): UserEntity
    suspend fun updateUserInfo(userInfoUpdateForm: UserInfoUpdateForm): UserEntity
    suspend fun deleteUserInfo(signUpForm: SignUpForm): UserEntity
    fun getUserInfo(): UserEntity
}
