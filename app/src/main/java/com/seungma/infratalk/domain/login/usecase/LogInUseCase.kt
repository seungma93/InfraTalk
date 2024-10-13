package com.seungma.infratalk.domain.login.usecase

import android.util.Log
import com.seungma.infratalk.domain.user.entity.UserEntity
import com.seungma.infratalk.domain.user.repository.UserDataRepository
import com.seungma.infratalk.presenter.sign.form.LoginForm
import javax.inject.Inject


class LoginUseCase @Inject constructor(private val repository: UserDataRepository) {
    suspend operator fun invoke(loginForm: LoginForm): UserEntity {
        Log.d("LgoinUseCase", "로그인 유즈케이스")
        return repository.login(loginForm = loginForm)
    }
}