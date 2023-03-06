package com.freetalk.repository

import com.freetalk.data.entity.UserEntity
import com.freetalk.data.entity.toEntity
import com.freetalk.data.remote.SignUpForm
import com.freetalk.data.remote.UpdateForm
import com.freetalk.data.remote.UserDataSource

interface UserDataRepository {
    suspend fun signUp(signUpForm: SignUpForm): UserEntity
    suspend fun logIn(userEntity: UserEntity): UserEntity
    suspend fun resetPassword(userData: UserEntity): UserEntity
    suspend fun sendVerifiedEmail(): UserEntity
    suspend fun updateUserInfo(updateForm: UpdateForm): UserEntity
}

class FirebaseUserDataRepositoryImpl(private val dataSource: UserDataSource): UserDataRepository{

    override suspend fun signUp(signUpForm: SignUpForm): UserEntity {
        return dataSource.signUp(signUpForm).toEntity()
    }

    override suspend fun logIn(userEntity: UserEntity): UserEntity {
        return dataSource.logIn(userEntity)
    }

    override suspend fun resetPassword(userEntity: UserEntity): UserEntity {
        return dataSource.resetPassword(userEntity)
    }

    override suspend fun sendVerifiedEmail(): UserEntity {
        return dataSource.sendVerifiedEmail().toEntity()
    }

    override suspend fun updateUserInfo(updateForm: UpdateForm): UserEntity {
        return dataSource.updateUserInfo(updateForm).toEntity()
    }
}