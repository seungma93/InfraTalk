package com.seungma.infratalk.domain.login.usecase

import com.seungma.infratalk.domain.user.entity.UserEntity
import com.seungma.infratalk.domain.user.repository.UserDataRepository
import com.seungma.infratalk.presenter.sign.form.ResetPasswordForm
import javax.inject.Inject


class ResetPasswordUseCase @Inject constructor(private val userDataRepository: UserDataRepository) {
    suspend operator fun invoke(resetPasswordForm: ResetPasswordForm): UserEntity {
        return userDataRepository.resetPassword(resetPasswordForm)
    }

}