package com.freetalk.usecase

import android.util.Log
import com.freetalk.data.entity.UserEntity
import com.freetalk.repository.UserDataRepository

interface SendEmailUseCase {
    suspend fun sendVerifiedEmail(): UserEntity
}

class SendEmailUseCaseImpl(private val repository: UserDataRepository): SendEmailUseCase {
    override suspend fun sendVerifiedEmail(): UserEntity {
        Log.d("SendEmailUseCase", "유즈케이스")
        return repository.sendVerifiedEmail()
    }

}