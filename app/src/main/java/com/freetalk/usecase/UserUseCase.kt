package com.freetalk.usecase

import com.freetalk.data.entity.UserEntity
import com.freetalk.repository.SignUpInfo
import com.freetalk.repository.UserDataRepository

interface UserUseCase {
    suspend fun signUp(userData: UserEntity): SignUpInfo
}

class UserUseCaseImpl(private val userDataRepository: UserDataRepository): UserUseCase {
    override suspend fun signUp(userData: UserEntity): SignUpInfo {
        return userDataRepository.signUp(userData)
    }

}