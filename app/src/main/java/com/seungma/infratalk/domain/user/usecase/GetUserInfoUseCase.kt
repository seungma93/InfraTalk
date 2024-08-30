package com.seungma.infratalk.domain.user.usecase

import com.seungma.infratalk.domain.user.repository.UserDataRepository
import com.seungma.infratalk.domain.user.entity.UserEntity
import javax.inject.Inject

class GetUserInfoUseCase @Inject constructor(private val userDataRepository: UserDataRepository) {
    operator fun invoke(): UserEntity {
        return userDataRepository.getUserInfo()
    }
}