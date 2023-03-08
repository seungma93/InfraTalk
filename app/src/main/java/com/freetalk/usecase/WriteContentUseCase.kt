package com.freetalk.usecase

import com.freetalk.data.entity.BoardEntity
import com.freetalk.data.remote.*
import com.freetalk.repository.BoardDataRepository

interface WriteContentUseCase {
    suspend fun insert(boardInsertForm: BoardInsetForm): BoardEntity
}

class WriteContentUseCaseImpl(private val repository: BoardDataRepository): WriteContentUseCase {
    override suspend fun insert(boardInsertForm: BoardInsetForm): BoardEntity {
        return repository.insert(boardInsertForm)
    }

}