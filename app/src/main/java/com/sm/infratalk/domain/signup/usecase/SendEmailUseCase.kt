package com.sm.infratalk.domain.signup.usecase

import com.sm.infratalk.data.FailSendEmailException
import com.sm.infratalk.domain.user.entity.UserEntity
import com.sm.infratalk.domain.user.repository.UserDataRepository
import javax.inject.Inject

class SendEmailUseCase @Inject constructor(private val repository: UserDataRepository) {
    suspend operator fun invoke(): UserEntity {
        return runCatching {
            repository.sendVerifiedEmail()
        }.onFailure {
            throw FailSendEmailException(_message = "이메일 발송 실패", throwable = it)
        }.getOrThrow()


    }

}