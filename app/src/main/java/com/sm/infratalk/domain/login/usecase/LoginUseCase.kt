package com.sm.infratalk.domain.login.usecase

import com.sm.infratalk.data.FailLoginException
import com.sm.infratalk.domain.user.entity.UserEntity
import com.sm.infratalk.domain.user.repository.UserDataRepository
import com.sm.infratalk.presenter.sign.form.LoginForm
import javax.inject.Inject


class LoginUseCase @Inject constructor(private val repository: UserDataRepository) {
    suspend operator fun invoke(loginForm: LoginForm): UserEntity {
        return runCatching {
            repository.login(loginForm = loginForm)
        }.onFailure {
            throw FailLoginException(_message = "로그인 실패", throwable = it)
        }.getOrThrow()
    }
}