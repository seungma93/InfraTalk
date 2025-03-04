package com.sm.infratalk.domain.user.usecase

import com.sm.infratalk.data.FailDeleteSavedEmailException
import com.sm.infratalk.domain.user.entity.SavedEmailGetEntity
import com.sm.infratalk.domain.user.repository.UserDataRepository
import com.sm.infratalk.presenter.sign.form.SavedEmailSetForm
import javax.inject.Inject

class DeleteSavedEmailUseCase @Inject constructor(private val userDataRepository: UserDataRepository) {
    operator fun invoke() {
        runCatching {
            userDataRepository.deleteSavedEmail()
        }.onFailure {
            throw FailDeleteSavedEmailException(_message = "저장된 이메일 삭제 실패", throwable = it)
        }
    }
}