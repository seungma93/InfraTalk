package com.seungma.infratalk.domain.user.usecase

import com.seungma.infratalk.data.FailGetSavedEmailException
import com.seungma.infratalk.domain.user.entity.SavedEmailGetEntity
import com.seungma.infratalk.domain.user.repository.UserDataRepository
import com.seungma.infratalk.presenter.sign.form.SavedEmailSetForm
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