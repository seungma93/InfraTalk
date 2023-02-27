package com.freetalk.repository

import com.freetalk.data.entity.BoardEntity
import com.freetalk.data.entity.UserEntity
import com.freetalk.data.remote.*

interface BoardDataRepository {
    suspend fun insert(boardEntity: BoardEntity): BoardInsertData
    suspend fun select(): BoardSelectData
}

class FirebaseBoardDataRepositoryImpl(private val dataSource: BoardDataSource): BoardDataRepository{
    override suspend fun insert(boardEntity: BoardEntity): BoardInsertData {
        return dataSource.insert(boardEntity)
    }

    override suspend fun select(): BoardSelectData {
        return dataSource.select()
    }

}