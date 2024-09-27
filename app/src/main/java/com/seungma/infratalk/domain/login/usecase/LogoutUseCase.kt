package com.seungma.infratalk.domain.login.usecase

import com.seungma.infratalk.domain.user.repository.UserDataRepository
import javax.inject.Inject


class LogoutUseCase @Inject constructor(private val repository: UserDataRepository) {
    operator fun invoke() {
        repository.logout()
    }
}