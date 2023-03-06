package com.freetalk.usecase

import com.freetalk.data.entity.UserEntity
import com.freetalk.data.remote.UpdateForm
import com.freetalk.repository.UserDataRepository

interface UpdateUserInfoUseCase {
    suspend fun updateUserInfo(updateForm: UpdateForm): UserEntity
}

class UpdateUserInfoUseCaseImpl(private val repository: UserDataRepository) :UpdateUserInfoUseCase{
    override suspend fun updateUserInfo(updateForm: UpdateForm): UserEntity {
        return repository.updateUserInfo(updateForm)
    }

}