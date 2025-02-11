package com.seungma.infratalk.domain.login.usecase

import com.seungma.infratalk.data.FailResetPasswordException
import com.seungma.infratalk.domain.user.entity.UserEntity
import com.seungma.infratalk.domain.user.repository.UserDataRepository
import com.seungma.infratalk.presenter.sign.form.ResetPasswordForm
import javax.inject.Inject


class ResetPasswordUseCase @Inject constructor(private val userDataRepository: UserDataRepository) {
    suspend operator fun invoke(resetPasswordForm: ResetPasswordForm): UserEntity {
        return runCatching {
            userDataRepository.resetPassword(resetPasswordForm)
        }.onFailure {
            throw FailResetPasswordException(_message = "패스워드 초기화 실패", throwable = it)
        }.getOrThrow()

    }

}