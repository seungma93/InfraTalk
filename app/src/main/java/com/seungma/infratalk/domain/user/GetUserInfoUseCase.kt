package com.seungma.infratalk.domain.user

import javax.inject.Inject

class GetUserInfoUseCase @Inject constructor(private val userDataRepository: UserDataRepository) {
    operator fun invoke(): UserEntity {
        return userDataRepository.getUserInfo()
    }
}