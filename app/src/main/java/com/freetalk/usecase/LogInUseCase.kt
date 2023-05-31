package com.freetalk.usecase

import android.util.Log
import com.freetalk.data.entity.UserEntity
import com.freetalk.data.remote.LogInForm
import com.freetalk.repository.UserDataRepository
import javax.inject.Inject

interface LogInUseCase {
    suspend fun logIn(logInForm: LogInForm): UserEntity
}

class LogInUseCaseImpl @Inject constructor(private val repository: UserDataRepository) : LogInUseCase {
    override suspend fun logIn(logInForm: LogInForm): UserEntity {
        Log.d("LgoinUseCase", "로그인 유즈케이스")
        return repository.logIn(logInForm)
    }
}