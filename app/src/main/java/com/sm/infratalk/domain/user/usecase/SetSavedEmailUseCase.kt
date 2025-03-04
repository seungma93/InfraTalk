package com.sm.infratalk.domain.user.usecase

import com.sm.infratalk.data.FailSetSavedEmailException
import com.sm.infratalk.domain.user.repository.UserDataRepository
import com.sm.infratalk.presenter.sign.form.SavedEmailSetForm
import javax.inject.Inject

class SetSavedEmailUseCase @Inject constructor(private val userDataRepository: UserDataRepository) {
    operator fun invoke(savedEmailSetForm: SavedEmailSetForm) {
        runCatching {
            userDataRepository.setSavedEmail(savedEmailSetForm = savedEmailSetForm)
        }.onFailure {
            throw FailSetSavedEmailException(_message = "이메일 저장 실패", throwable = it)
        }

    }
}