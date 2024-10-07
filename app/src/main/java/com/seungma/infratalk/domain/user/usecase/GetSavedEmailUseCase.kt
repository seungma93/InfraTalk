package com.seungma.infratalk.domain.user.usecase

import com.seungma.infratalk.domain.user.entity.SavedEmailGetEntity
import com.seungma.infratalk.domain.user.repository.UserDataRepository
import com.seungma.infratalk.presenter.sign.form.SavedEmailSetForm
import javax.inject.Inject

class GetSavedEmailUseCase @Inject constructor(private val userDataRepository: UserDataRepository) {
    operator fun invoke(): SavedEmailGetEntity {
        return userDataRepository.getSavedEmail()
    }
}