package com.sm.infratalk.domain.user.usecase

import com.sm.infratalk.data.FailGetSavedEmailException
import com.sm.infratalk.domain.user.entity.SavedEmailGetEntity
import com.sm.infratalk.domain.user.repository.UserDataRepository
import com.sm.infratalk.presenter.sign.form.SavedEmailSetForm
import javax.inject.Inject

class GetSavedEmailUseCase @Inject constructor(private val userDataRepository: UserDataRepository) {
    operator fun invoke(): SavedEmailGetEntity {
        return runCatching {
            userDataRepository.getSavedEmail()
        }.onFailure {
            throw FailGetSavedEmailException(_message = "저장된 이메일 가져오기 실패", throwable = it)
        }.getOrThrow()
    }
}