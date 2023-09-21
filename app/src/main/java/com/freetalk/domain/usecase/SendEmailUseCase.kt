package com.freetalk.domain.usecase

import android.util.Log
import com.freetalk.domain.entity.UserEntity
import com.freetalk.domain.repository.UserDataRepository
import javax.inject.Inject

interface SendEmailUseCase {
    suspend fun sendVerifiedEmail(): UserEntity
}

class SendEmailUseCaseImpl @Inject constructor(private val repository: UserDataRepository): SendEmailUseCase {
    override suspend fun sendVerifiedEmail(): UserEntity {
        Log.d("SendEmailUseCase", "유즈케이스")
        return repository.sendVerifiedEmail()
    }

}