package com.freetalk.domain.usecase

import android.util.Log
import com.freetalk.domain.entity.UserEntity
import com.freetalk.domain.repository.UserDataRepository
import com.freetalk.presenter.form.LogInForm
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