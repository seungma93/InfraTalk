package com.freetalk.usecase

import com.freetalk.data.entity.UserEntity
import com.freetalk.data.remote.AuthData
import com.freetalk.repository.UserDataRepository

interface UserUseCase {
    suspend fun signUp(userData: UserEntity): AuthData
    suspend fun logIn(userData: UserEntity): AuthData
    suspend fun resetPassword(userData: UserEntity): AuthData
}

class UserUseCaseImpl(private val userDataRepository: UserDataRepository): UserUseCase {
    override suspend fun signUp(userData: UserEntity): AuthData {
        return userDataRepository.signUp(userData)
    }

    override suspend fun logIn(userData: UserEntity): AuthData {
        return userDataRepository.logIn(userData)
    }

    override suspend fun resetPassword(userData: UserEntity): AuthData {
        return userDataRepository.resetPassword(userData)
    }

}