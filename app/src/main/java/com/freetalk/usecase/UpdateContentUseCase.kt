package com.freetalk.usecase

import com.freetalk.data.entity.BoardEntity
import com.freetalk.data.remote.BoardUpdateForm
import com.freetalk.repository.BoardDataRepository

interface UpdateContentUseCase {
    suspend fun updateContent(boardUpdateForm: BoardUpdateForm): BoardEntity
}

class UpdateContentUseCaseImpl(private val boardDataRepository: BoardDataRepository) : UpdateContentUseCase{
    override suspend fun updateContent(boardUpdateForm: BoardUpdateForm): BoardEntity {
        return boardDataRepository.update(boardUpdateForm)
    }

}