package com.freetalk.domain.usecase

import com.freetalk.domain.entity.BoardMetaEntity
import com.freetalk.domain.repository.BoardDataRepository
import com.freetalk.presenter.form.BoardUpdateForm
import javax.inject.Inject

class UpdateBoardContentUseCase @Inject constructor(private val boardDataRepository: BoardDataRepository) {
    suspend operator fun invoke(boardUpdateForm: BoardUpdateForm): BoardMetaEntity {
        return boardDataRepository.updateBoard(boardUpdateForm)
    }
}