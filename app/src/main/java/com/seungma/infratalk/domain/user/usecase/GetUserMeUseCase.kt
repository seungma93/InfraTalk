package com.seungma.infratalk.domain.user.usecase

import com.seungma.infratalk.domain.user.entity.UserEntity
import com.seungma.infratalk.domain.user.repository.UserDataRepository
import javax.inject.Inject

class GetUserMeUseCase @Inject constructor(private val userDataRepository: UserDataRepository) {
    suspend operator fun invoke(): UserEntity {
        return userDataRepository.getUserMe()
    }
}