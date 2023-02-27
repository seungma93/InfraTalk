package com.freetalk.usecase

import com.freetalk.data.entity.BoardEntity
import com.freetalk.data.entity.UserEntity
import com.freetalk.data.remote.*
import com.freetalk.repository.BoardDataRepository
import com.freetalk.repository.UserDataRepository

interface BoardUseCase {
    suspend fun insert(boardEntity: BoardEntity): BoardInsertData
    suspend fun select(): BoardSelectData
}

class BoardUseCaseImpl(private val BoardDataRepository: BoardDataRepository): BoardUseCase {
    override suspend fun insert(boardEntity: BoardEntity): BoardInsertData {
        return BoardDataRepository.insert(boardEntity)
    }

    override suspend fun select(): BoardSelectData {
        return BoardDataRepository.select()
    }

}