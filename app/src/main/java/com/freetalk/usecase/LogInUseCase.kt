package com.freetalk.usecase

import com.freetalk.data.entity.UserEntity
import com.freetalk.repository.UserDataRepository

interface LogInUseCase {
    suspend fun logIn(userEntity: UserEntity): UserEntity
}

class LogInUseCaseImpl(private val repository: UserDataRepository) : LogInUseCase {
    override suspend fun logIn(userEntity: UserEntity): UserEntity {
        return repository.logIn(userEntity)
    }

}