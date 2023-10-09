package com.freetalk.domain.usecase

import com.freetalk.data.FailInsertException
import com.freetalk.domain.entity.BoardInsertEntity
import com.freetalk.domain.entity.BoardMetaEntity
import com.freetalk.domain.repository.BoardDataRepository
import com.freetalk.presenter.form.BoardContentInsertForm
import javax.inject.Inject

class WriteBoardContentUseCase @Inject constructor(private val repository: BoardDataRepository) {
    suspend operator fun invoke(boardContentInsertForm: BoardContentInsertForm): BoardInsertEntity {
        val boardInsertEntity = repository.insertBoard(boardContentInsertForm)
        return when(boardInsertEntity.isSuccess) {
            true -> boardInsertEntity
            false -> throw FailInsertException("인서트 실패")
        }
    }
}