package com.freetalk.usecase

import com.freetalk.data.entity.UserEntity
import com.freetalk.data.remote.UpdateForm
import com.freetalk.repository.UserDataRepository
import javax.inject.Inject

interface UpdateUserInfoUseCase {
    suspend fun updateUserInfo(updateForm: UpdateForm): UserEntity
}

class UpdateUserInfoUseCaseImpl @Inject constructor(private val repository: UserDataRepository) :UpdateUserInfoUseCase{
    override suspend fun updateUserInfo(updateForm: UpdateForm): UserEntity {
        return repository.updateUserInfo(updateForm)
    }

}