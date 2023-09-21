package com.freetalk.domain.usecase

import com.freetalk.domain.entity.UserEntity
import com.freetalk.domain.repository.UserDataRepository
import com.freetalk.presenter.form.SignUpForm
import javax.inject.Inject

interface DeleteUserInfoUseCase {
    suspend fun deleteUserInfo(signUpForm: SignUpForm): UserEntity
}

class DeleteUserInfoUseCaseImpl @Inject constructor(private val repository: UserDataRepository) :DeleteUserInfoUseCase{
    override suspend fun deleteUserInfo(signUpForm: SignUpForm): UserEntity {
        return repository.deleteUserInfo(signUpForm)
    }

}