package com.freetalk.usecase

import com.freetalk.data.entity.BoardEntity
import com.freetalk.data.remote.*
import com.freetalk.repository.BoardDataRepository
import javax.inject.Inject

interface WriteContentUseCase {
    suspend fun insert(boardInsertForm: BoardInsetForm): BoardEntity
}

class WriteContentUseCaseImpl @Inject constructor(private val repository: BoardDataRepository): WriteContentUseCase {
    override suspend fun insert(boardInsertForm: BoardInsetForm): BoardEntity {
        return repository.insert(boardInsertForm)
    }

}