package com.freetalk.repository

import android.util.Log
import com.freetalk.data.entity.UserEntity
import com.freetalk.data.entity.toEntity
import com.freetalk.data.remote.*

interface UserDataRepository {
    suspend fun signUp(signUpForm: SignUpForm): UserEntity
    suspend fun logIn(logInForm: LogInForm): UserEntity
    suspend fun resetPassword(resetPasswordForm: ResetPasswordForm): UserEntity
    suspend fun sendVerifiedEmail(): UserEntity
    suspend fun updateUserInfo(updateForm: UpdateForm): UserEntity
}

class FirebaseUserDataRepositoryImpl(private val dataSource: UserDataSource): UserDataRepository{

    override suspend fun signUp(signUpForm: SignUpForm): UserEntity {
        return dataSource.signUp(signUpForm).toEntity()
    }

    override suspend fun logIn(logInForm: LogInForm): UserEntity {
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
}