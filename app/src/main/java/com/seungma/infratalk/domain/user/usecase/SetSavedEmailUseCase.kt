package com.seungma.infratalk.domain.user.usecase

import com.seungma.infratalk.domain.user.repository.UserDataRepository
import com.seungma.infratalk.presenter.sign.form.SavedEmailSetForm
import javax.inject.Inject

class SetSavedEmailUseCase @Inject constructor(private val userDataRepository: UserDataRepository) {
    operator fun invoke(savedEmailSetForm: SavedEmailSetForm) {
        userDataRepository.setSavedEmail(savedEmailSetForm = savedEmailSetForm)
    }
}