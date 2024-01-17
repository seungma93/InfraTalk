package com.seungma.infratalk.domain.board.usecase

import com.seungma.infratalk.domain.board.entity.BoardInsertEntity
import com.seungma.infratalk.domain.board.repository.BoardDataRepository
import com.seungma.infratalk.presenter.board.form.BoardContentInsertForm
import javax.inject.Inject

class WriteBoardContentUseCase @Inject constructor(private val repository: BoardDataRepository) {
    suspend operator fun invoke(boardContentInsertForm: BoardContentInsertForm): BoardInsertEntity {
        val boardInsertEntity = repository.insertBoard(boardContentInsertForm)
        return when (boardInsertEntity.isSuccess) {
            true -> boardInsertEntity
            false -> throw com.seungma.infratalk.data.FailInsertException("인서트 실패")
        }
    }
}