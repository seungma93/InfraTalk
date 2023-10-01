package com.freetalk.domain.usecase

import com.freetalk.domain.entity.UserEntity
import com.freetalk.domain.repository.UserDataRepository
import javax.inject.Inject

class GetUserInfoUseCase @Inject constructor(private val userDataRepository: UserDataRepository) {
    operator fun invoke(): UserEntity{
        return userDataRepository.getUserInfo()
    }
}