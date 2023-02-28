package com.freetalk.repository

import com.freetalk.data.entity.UserEntity
import com.freetalk.data.remote.AuthData
import com.freetalk.data.remote.LoginData
import com.freetalk.data.remote.UserDataSource

interface UserDataRepository {
    suspend fun signUp(userData: UserEntity): AuthData
    suspend fun logIn(userData: UserEntity): LoginData
    suspend fun resetPassword(userData: UserEntity): AuthData
}

class FirebaseUserDataRepositoryImpl(private val dataSource: UserDataSource): UserDataRepository{

    override suspend fun signUp(userData: UserEntity): AuthData {
        return dataSource.signUp(userData)
    }

    override suspend fun logIn(userData: UserEntity): LoginData {
        return dataSource.logIn(userData)
    }

    override suspend fun resetPassword(userData: UserEntity): AuthData {
        return dataSource.resetPassword(userData)
    }
}