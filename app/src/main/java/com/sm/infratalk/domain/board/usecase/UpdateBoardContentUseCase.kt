package com.sm.infratalk.domain.board.usecase

import com.sm.infratalk.domain.board.entity.BoardMetaEntity
import com.sm.infratalk.domain.board.repository.BoardDataRepository
import com.sm.infratalk.presenter.board.form.BoardUpdateForm
import javax.inject.Inject

class UpdateBoardContentUseCase @Inject constructor(private val boardDataRepository: BoardDataRepository) {
    suspend operator fun invoke(boardUpdateForm: BoardUpdateForm): BoardMetaEntity {
        return boardDataRepository.updateBoard(boardUpdateForm)
    }
}