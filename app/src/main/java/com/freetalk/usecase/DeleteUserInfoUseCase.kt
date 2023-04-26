package com.freetalk.usecase

import com.freetalk.data.entity.UserEntity
import com.freetalk.data.remote.SignUpForm
import com.freetalk.data.remote.UpdateForm
import com.freetalk.repository.UserDataRepository
import javax.inject.Inject

interface DeleteUserInfoUseCase {
    suspend fun deleteUserInfo(signUpForm: SignUpForm): UserEntity
}

class DeleteUserInfoUseCaseImpl @Inject constructor(private val repository: UserDataRepository) :DeleteUserInfoUseCase{
    override suspend fun deleteUserInfo(signUpForm: SignUpForm): UserEntity {
        return repository.deleteUserInfo(signUpForm)
    }

}