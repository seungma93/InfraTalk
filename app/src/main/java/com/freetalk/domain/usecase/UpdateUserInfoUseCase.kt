package com.freetalk.domain.usecase

import com.freetalk.domain.entity.UserEntity
import com.freetalk.domain.repository.UserDataRepository
import com.freetalk.presenter.form.UpdateForm
import javax.inject.Inject

interface UpdateUserInfoUseCase {
    suspend fun updateUserInfo(updateForm: UpdateForm): UserEntity
}

class UpdateUserInfoUseCaseImpl @Inject constructor(private val repository: UserDataRepository) :UpdateUserInfoUseCase{
    override suspend fun updateUserInfo(updateForm: UpdateForm): UserEntity {
        return repository.updateUserInfo(updateForm)
    }

}