package com.seungma.infratalk.domain.signup.usecase

import com.seungma.infratalk.data.FailDeleteUserInfoException
import com.seungma.infratalk.domain.user.entity.UserEntity
import com.seungma.infratalk.domain.user.repository.UserDataRepository
import com.seungma.infratalk.presenter.sign.form.SignUpForm
import javax.inject.Inject


class DeleteUserInfoUseCase @Inject constructor(private val repository: UserDataRepository) {
    suspend operator fun invoke(signUpForm: SignUpForm): UserEntity {
        return runCatching {
            repository.deleteUserInfo(signUpForm)
        }.onFailure {
            throw FailDeleteUserInfoException(_message = "유저 정보 삭제 실패", throwable = it)
        }.getOrThrow()

    }

}