package com.freetalk.usecase

import com.freetalk.data.entity.BoardEntity
import com.freetalk.data.entity.UserEntity
import com.freetalk.data.remote.AuthData
import com.freetalk.data.remote.BoardData
import com.freetalk.data.remote.Respond
import com.freetalk.repository.BoardDataRepository
import com.freetalk.repository.UserDataRepository

interface BoardUseCase {
    suspend fun insert(boardEntity: BoardEntity): Respond
    suspend fun select(): BoardData
}

class BoardUseCaseImpl(private val BoardDataRepository: BoardDataRepository): BoardUseCase {
    override suspend fun insert(boardEntity: BoardEntity): Respond {
        return BoardDataRepository.insert(boardEntity)
    }

    override suspend fun select(): BoardData {
        return BoardDataRepository.select()
    }

}