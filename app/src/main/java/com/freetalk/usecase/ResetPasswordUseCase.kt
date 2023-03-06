package com.freetalk.usecase

import com.freetalk.data.entity.UserEntity
import com.freetalk.presenter.viewmodel.ViewEvent
import com.freetalk.repository.UserDataRepository

interface ResetPasswordUseCase {
    suspend fun resetPassword(userEntity: UserEntity): UserEntity
}

class ResetPasswordUseCaseImpl(private val userDataRepository: UserDataRepository) : ResetPasswordUseCase {
    override suspend fun resetPassword(userEntity: UserEntity): UserEntity {
        return userDataRepository.resetPassword(userEntity)
    }

}