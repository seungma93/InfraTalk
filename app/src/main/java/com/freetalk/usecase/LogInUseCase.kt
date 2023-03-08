package com.freetalk.usecase

import com.freetalk.data.entity.UserEntity
import com.freetalk.data.remote.LogInForm
import com.freetalk.repository.UserDataRepository

interface LogInUseCase {
    suspend fun logIn(logInForm: LogInForm): UserEntity
}

class LogInUseCaseImpl(private val repository: UserDataRepository) : LogInUseCase {
    override suspend fun logIn(logInForm: LogInForm): UserEntity {
        return repository.logIn(logInForm)
    }
}