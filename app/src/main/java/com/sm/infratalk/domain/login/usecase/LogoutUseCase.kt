package com.sm.infratalk.domain.login.usecase

import com.sm.infratalk.data.FailLogoutException
import com.sm.infratalk.domain.user.repository.UserDataRepository
import javax.inject.Inject


class LogoutUseCase @Inject constructor(private val repository: UserDataRepository) {
    operator fun invoke() {
        runCatching {
            repository.logout()
        }.onFailure {
            throw FailLogoutException(_message = "로그아웃 실패", throwable = it)
        }

    }
}