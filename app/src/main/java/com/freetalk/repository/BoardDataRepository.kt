package com.freetalk.repository

import com.freetalk.data.entity.BoardEntity
import com.freetalk.data.entity.UserEntity
import com.freetalk.data.remote.AuthData
import com.freetalk.data.remote.BoardDataSource
import com.freetalk.data.remote.Respond
import com.freetalk.data.remote.UserDataSource

interface BoardDataRepository {
    suspend fun insert(boardEntity: BoardEntity): Respond

}

class FirebaseBoardDataRepositoryImpl(private val dataSource: BoardDataSource): BoardDataRepository{
    override suspend fun insert(boardEntity: BoardEntity): Respond {
        return dataSource.insert(boardEntity)
    }


}