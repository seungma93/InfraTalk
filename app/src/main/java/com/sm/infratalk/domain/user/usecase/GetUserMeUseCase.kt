package com.sm.infratalk.domain.user.usecase

import com.sm.infratalk.domain.user.entity.UserEntity
import com.sm.infratalk.domain.user.repository.UserDataRepository
import javax.inject.Inject

class GetUserMeUseCase @Inject constructor(private val userDataRepository: UserDataRepository) {
    suspend operator fun invoke(): UserEntity {
        return userDataRepository.getUserMe()

    }
}