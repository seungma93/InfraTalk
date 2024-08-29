package com.seungma.infratalk.domain.login.usecase

import android.util.Log
import com.seungma.infratalk.domain.user.repository.UserDataRepository
import com.seungma.infratalk.domain.user.entity.UserEntity
import com.seungma.infratalk.presenter.sign.form.LoginForm
import javax.inject.Inject

interface LogInUseCase {
    suspend fun logIn(logInForm: LoginForm): UserEntity
}

class LogInUseCaseImpl @Inject constructor(private val repository: UserDataRepository) :
    LogInUseCase {
    override suspend fun logIn(logInForm: LoginForm): UserEntity {
        Log.d("LgoinUseCase", "로그인 유즈케이스")
        return repository.logIn(logInForm)
    }
}