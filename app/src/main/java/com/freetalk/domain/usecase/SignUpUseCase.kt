package com.freetalk.domain.usecase

import android.util.Log
import com.freetalk.domain.entity.UserEntity
import com.freetalk.domain.repository.UserDataRepository
import com.freetalk.presenter.form.SignUpForm
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