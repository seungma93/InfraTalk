package com.seungma.infratalk.domain.signup.usecase

import android.util.Log
import com.seungma.infratalk.data.FailSignupException
import com.seungma.infratalk.domain.user.repository.UserDataRepository
import com.seungma.infratalk.domain.user.entity.UserEntity
import com.seungma.infratalk.presenter.sign.form.SignUpForm
import javax.inject.Inject


class SignUpUseCase @Inject constructor(private val userDataRepository: UserDataRepository){
    suspend operator fun invoke(signUpForm: SignUpForm): UserEntity {
        return runCatching {
            userDataRepository.signUp(signUpForm)
        }.onFailure {
            throw FailSignupException(_message = "회원 가입 실패", throwable = it)
        }.getOrThrow()
    }
}