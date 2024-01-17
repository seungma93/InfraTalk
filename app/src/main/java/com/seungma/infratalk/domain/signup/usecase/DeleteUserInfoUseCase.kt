package com.seungma.infratalk.domain.signup.usecase

import com.seungma.infratalk.domain.user.UserDataRepository
import com.seungma.infratalk.domain.user.UserEntity
import com.seungma.infratalk.presenter.sign.form.SignUpForm
import javax.inject.Inject

interface DeleteUserInfoUseCase {
    suspend fun deleteUserInfo(signUpForm: SignUpForm): UserEntity
}

class DeleteUserInfoUseCaseImpl @Inject constructor(private val repository: UserDataRepository) :
    DeleteUserInfoUseCase {
    override suspend fun deleteUserInfo(signUpForm: SignUpForm): UserEntity {
        return repository.deleteUserInfo(signUpForm)
    }

}