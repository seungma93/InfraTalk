package com.sm.infratalk.domain.signup.usecase

import com.sm.infratalk.data.FailDeleteUserInfoException
import com.sm.infratalk.domain.user.entity.UserEntity
import com.sm.infratalk.domain.user.repository.UserDataRepository
import com.sm.infratalk.presenter.sign.form.SignUpForm
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