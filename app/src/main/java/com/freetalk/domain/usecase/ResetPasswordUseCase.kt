package com.freetalk.domain.usecase

import com.freetalk.domain.entity.UserEntity
import com.freetalk.domain.repository.UserDataRepository
import com.freetalk.presenter.form.ResetPasswordForm
import javax.inject.Inject

interface ResetPasswordUseCase {
    suspend fun resetPassword(resetPasswordForm: ResetPasswordForm): UserEntity
}

class ResetPasswordUseCaseImpl @Inject constructor(private val userDataRepository: UserDataRepository) : ResetPasswordUseCase {
    override suspend fun resetPassword(resetPasswordForm: ResetPasswordForm): UserEntity {
        return userDataRepository.resetPassword(resetPasswordForm)
    }

}