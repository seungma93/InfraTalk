package com.freetalk.domain.usecase

import com.freetalk.domain.entity.BoardMetaEntity
import com.freetalk.domain.repository.BoardDataRepository
import com.freetalk.presenter.form.BoardContentInsertForm
import javax.inject.Inject

class WriteBoardContentUseCase @Inject constructor(private val repository: BoardDataRepository) {
    suspend operator fun invoke(boardContentInsertForm: BoardContentInsertForm): BoardMetaEntity {
        return repository.insertBoard(boardContentInsertForm)
    }
}