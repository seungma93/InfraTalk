package com.seungma.domain.repository

import android.util.Log
import com.seungma.infratalk.data.datasource.remote.UserDataSource
import com.seungma.infratalk.data.model.request.user.UserInfoUpdateRequest
import com.seungma.infratalk.domain.user.UserDataRepository
import com.seungma.infratalk.domain.user.UserEntity
import com.seungma.infratalk.presenter.sign.form.LoginForm
import com.seungma.infratalk.presenter.sign.form.ResetPasswordForm
import com.seungma.infratalk.presenter.sign.form.SignUpForm
import com.seungma.infratalk.presenter.sign.form.UserInfoUpdateForm
import toEntity
import javax.inject.Inject

class UserDataRepositoryImpl @Inject constructor(private val dataSource: UserDataSource) :
    UserDataRepository {

    override suspend fun signUp(signUpForm: SignUpForm): UserEntity {
        Log.d("UserDataR", "시작")
        return dataSource.signUp(signUpForm).toEntity()
    }

    override suspend fun logIn(logInForm: LoginForm): UserEntity {
        Log.d("UserDataR", "로그인 레포")
        return dataSource.login(logInForm).toEntity()
    }

    override suspend fun resetPassword(resetPasswordForm: ResetPasswordForm): UserEntity {
        return dataSource.resetPassword(resetPasswordForm).toEntity()
    }

    override suspend fun sendVerifiedEmail(): UserEntity {
        Log.d("SendEmail", "레포지토리")
        return dataSource.sendVerifiedEmail().toEntity()
    }

    override suspend fun updateUserInfo(userInfoUpdateForm: UserInfoUpdateForm): UserEntity =
        with(userInfoUpdateForm) {
            return dataSource.updateUserInfo(
                userInfoUpdateRequest = UserInfoUpdateRequest(
                    email = email,
                    nickname = nickname,
                    image = image
                )
            ).toEntity()
        }

    override suspend fun deleteUserInfo(signUpForm: SignUpForm): UserEntity {
        return dataSource.deleteUserInfo(signUpForm).toEntity()
    }

    override fun getUserInfo(): UserEntity {
        return dataSource.getUserInfo()
    }


}