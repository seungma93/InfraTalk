package com.freetalk.domain.usecase

import com.freetalk.domain.entity.UserEntity
import com.freetalk.domain.repository.UserDataRepository
import com.freetalk.presenter.form.UserInfoUpdateForm
import javax.inject.Inject

class UpdateUserInfoUseCase @Inject constructor(private val repository: UserDataRepository) {
    suspend operator fun invoke(userInfoUpdateForm: UserInfoUpdateForm): UserEntity {
        return repository.updateUserInfo(userInfoUpdateForm)
    }

}