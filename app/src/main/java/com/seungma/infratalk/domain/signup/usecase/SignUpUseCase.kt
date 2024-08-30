package com.seungma.infratalk.domain.signup.usecase

import android.util.Log
import com.seungma.infratalk.domain.user.repository.UserDataRepository
import com.seungma.infratalk.domain.user.entity.UserEntity
import com.seungma.infratalk.presenter.sign.form.SignUpForm
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