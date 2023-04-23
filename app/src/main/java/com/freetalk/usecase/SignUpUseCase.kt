package com.freetalk.usecase

import android.util.Log
import com.freetalk.data.entity.UserEntity
import com.freetalk.data.remote.SignUpForm
import com.freetalk.repository.UserDataRepository
import javax.inject.Inject


interface SignUpUseCase {
    suspend fun signUp(signUpForm: SignUpForm): UserEntity
}

class SignUpUseCaseImpl @Inject constructor(
    private val userDataRepository: UserDataRepository
) : SignUpUseCase {
    override suspend fun signUp(signUpForm: SignUpForm): UserEntity {
        Log.d("SignUpU", "시작")
        return userDataRepository.signUp(signUpForm)
    }
}