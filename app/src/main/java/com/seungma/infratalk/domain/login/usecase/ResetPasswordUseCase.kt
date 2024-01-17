package com.seungma.infratalk.domain.login.usecase

import com.seungma.infratalk.domain.user.UserDataRepository
import com.seungma.infratalk.domain.user.UserEntity
import com.seungma.infratalk.presenter.sign.form.ResetPasswordForm
import javax.inject.Inject

interface ResetPasswordUseCase {
    suspend fun resetPassword(resetPasswordForm: ResetPasswordForm): UserEntity
}

class ResetPasswordUseCaseImpl @Inject constructor(private val userDataRepository: UserDataRepository) :
    ResetPasswordUseCase {
    override suspend fun resetPassword(resetPasswordForm: ResetPasswordForm): UserEntity {
        return userDataRepository.resetPassword(resetPasswordForm)
    }

}