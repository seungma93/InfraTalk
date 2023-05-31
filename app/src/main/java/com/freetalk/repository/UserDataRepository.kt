package com.freetalk.repository

import android.util.Log
import com.freetalk.data.entity.BoardEntity
import com.freetalk.data.entity.UserEntity
import com.freetalk.data.entity.toEntity
import com.freetalk.data.remote.*
import javax.inject.Inject
import javax.inject.Singleton

interface UserDataRepository {
    suspend fun signUp(signUpForm: SignUpForm): UserEntity
    suspend fun logIn(logInForm: LogInForm): UserEntity
    suspend fun resetPassword(resetPasswordForm: ResetPasswordForm): UserEntity
    suspend fun sendVerifiedEmail(): UserEntity
    suspend fun updateUserInfo(updateForm: UpdateForm): UserEntity
    suspend fun deleteUserInfo(signUpForm: SignUpForm): UserEntity
    suspend fun updateBookMark(bookMarkUpdateForm: BookMarkUpdateForm): UserEntity
}

class UserDataRepositoryImpl @Inject constructor(private val dataSource: UserDataSource): UserDataRepository{

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

    override suspend fun updateBookMark(bookMarkUpdateForm: BookMarkUpdateForm): UserEntity {
        return dataSource.updateBookMarkList(bookMarkUpdateForm).toEntity()
    }
}