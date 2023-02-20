package com.freetalk.usecase

import com.freetalk.data.entity.BoardEntity
import com.freetalk.data.entity.UserEntity
import com.freetalk.data.remote.AuthData
import com.freetalk.data.remote.Respond
import com.freetalk.repository.BoardDataRepository
import com.freetalk.repository.UserDataRepository

interface BoardUseCase {
    suspend fun insert(boardEntity: BoardEntity): Respond
}

class BoardUseCaseImpl(private val BoardDataRepository: BoardDataRepository): BoardUseCase {
    override suspend fun insert(boardEntity: BoardEntity): Respond {
        return BoardDataRepository.insert(boardEntity)
    }

}