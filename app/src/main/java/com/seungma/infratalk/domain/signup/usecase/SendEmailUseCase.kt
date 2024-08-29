package com.seungma.infratalk.domain.signup.usecase

import android.util.Log
import com.seungma.infratalk.domain.user.repository.UserDataRepository
import com.seungma.infratalk.domain.user.entity.UserEntity
import javax.inject.Inject

interface SendEmailUseCase {
    suspend fun sendVerifiedEmail(): UserEntity
}

class SendEmailUseCaseImpl @Inject constructor(private val repository: UserDataRepository) :
    SendEmailUseCase {
    override suspend fun sendVerifiedEmail(): UserEntity {
        Log.d("SendEmailUseCase", "유즈케이스")
        return repository.sendVerifiedEmail()
    }

}