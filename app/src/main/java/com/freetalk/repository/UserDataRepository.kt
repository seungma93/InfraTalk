package com.freetalk.repository

import com.freetalk.data.entity.UserEntity
import com.freetalk.data.remote.AuthData
import com.freetalk.data.remote.UserDataSource

interface UserDataRepository {
    suspend fun signUp(userData: UserEntity): AuthData
    suspend fun logIn(userData: UserEntity): AuthData
}

class FirebaseUserDataRepositoryImpl(private val dataSource: UserDataSource<AuthData>): UserDataRepository{

    override suspend fun signUp(userData: UserEntity): AuthData {
        return dataSource.signUp(userData)
    }

    override suspend fun logIn(userData: UserEntity): AuthData {
        return dataSource.logIn(userData)
    }
}