package com.freetalk.domain.repository

import android.util.Log
import com.freetalk.data.datasource.remote.UserDataSource
import com.freetalk.domain.entity.UserEntity
import com.freetalk.presenter.form.LogInForm
import com.freetalk.presenter.form.ResetPasswordForm
import com.freetalk.presenter.form.SignUpForm
import com.freetalk.presenter.form.UpdateForm
import toEntity
import javax.inject.Inject

class UserDataRepositoryImpl @Inject constructor(private val dataSource: UserDataSource):
    UserDataRepository {

    override suspend fun signUp(signUpForm: SignUpForm): UserEntity {
        Log.d("UserDataR", "시작")
        return dataSource.signUp(signUpForm).toEntity()
    }

    override suspend fun logIn(logInForm: LogInForm): UserEntity {
        Log.d("UserDataR", "로그인 레포")
        return dataSource.logIn(logInForm).toEntity()
    }

    override suspend fun resetPassword(resetPasswordForm: ResetPasswordForm): UserEntity {
        return dataSource.resetPassword(resetPasswordForm).toEntity()
    }

    override suspend fun sendVerifiedEmail(): UserEntity {
        Log.d("SendEmail", "레포지토리")
        return dataSource.sendVerifiedEmail().toEntity()
    }

    override suspend fun updateUserInfo(updateForm: UpdateForm): UserEntity {
        return dataSource.updateUserInfo(updateForm).toEntity()
    }

    override suspend fun deleteUserInfo(signUpForm: SignUpForm): UserEntity {
        return dataSource.deleteUserInfo(signUpForm).toEntity()
    }

}