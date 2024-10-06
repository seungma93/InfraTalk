package com.seungma.infratalk.domain.user.usecase

import com.seungma.infratalk.domain.user.repository.UserDataRepository
import com.seungma.infratalk.presenter.sign.form.SavedEmailSetForm
import javax.inject.Inject

class SetSavedEmailUseCase @Inject constructor(private val userDataRepository: UserDataRepository) {
    suspend operator fun invoke(savedEmailSetForm: SavedEmailSetForm) {
        return userDataRepository.setSavedEmail(savedEmailSetForm = savedEmailSetForm)

    }
}