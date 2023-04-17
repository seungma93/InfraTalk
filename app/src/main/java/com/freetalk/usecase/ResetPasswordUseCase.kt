package com.freetalk.usecase

import com.freetalk.data.entity.UserEntity
import com.freetalk.data.remote.ResetPasswordForm
import com.freetalk.presenter.viewmodel.ViewEvent
import com.freetalk.repository.UserDataRepository
import javax.inject.Inject

interface ResetPasswordUseCase {
    suspend fun resetPassword(resetPasswordForm: ResetPasswordForm): UserEntity
}

class ResetPasswordUseCaseImpl @Inject constructor(private val userDataRepository: UserDataRepository) : ResetPasswordUseCase {
    override suspend fun resetPassword(resetPasswordForm: ResetPasswordForm): UserEntity {
        return userDataRepository.resetPassword(resetPasswordForm)
    }

}