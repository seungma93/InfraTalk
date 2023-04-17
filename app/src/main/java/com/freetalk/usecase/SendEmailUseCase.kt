package com.freetalk.usecase

import android.util.Log
import com.freetalk.data.entity.UserEntity
import com.freetalk.repository.UserDataRepository
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