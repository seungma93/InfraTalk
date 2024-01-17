package com.seungma.infratalk.domain.login.usecase

import android.util.Log
import com.seungma.infratalk.domain.user.UserDataRepository
import com.seungma.infratalk.domain.user.UserEntity
import com.seungma.infratalk.presenter.sign.form.LogInForm
import javax.inject.Inject

interface LogInUseCase {
    suspend fun logIn(logInForm: LogInForm): UserEntity
}

class LogInUseCaseImpl @Inject constructor(private val repository: UserDataRepository) :
    LogInUseCase {
    override suspend fun logIn(logInForm: LogInForm): UserEntity {
        Log.d("LgoinUseCase", "로그인 유즈케이스")
        return repository.logIn(logInForm)
    }
}