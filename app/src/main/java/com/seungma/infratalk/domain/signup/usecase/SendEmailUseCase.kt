package com.seungma.infratalk.domain.signup.usecase

import com.seungma.infratalk.domain.user.entity.UserEntity
import com.seungma.infratalk.domain.user.repository.UserDataRepository
import javax.inject.Inject

class SendEmailUseCase @Inject constructor(private val repository: UserDataRepository) {
    suspend operator fun invoke(): UserEntity {
        return repository.sendVerifiedEmail()
    }

}