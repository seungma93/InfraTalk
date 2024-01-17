package com.seungma.infratalk.domain.board.usecase

import com.seungma.infratalk.domain.board.entity.BoardMetaEntity
import com.seungma.infratalk.domain.board.repository.BoardDataRepository
import com.seungma.infratalk.presenter.board.form.BoardUpdateForm
import javax.inject.Inject

class UpdateBoardContentUseCase @Inject constructor(private val boardDataRepository: BoardDataRepository) {
    suspend operator fun invoke(boardUpdateForm: BoardUpdateForm): BoardMetaEntity {
        return boardDataRepository.updateBoard(boardUpdateForm)
    }
}