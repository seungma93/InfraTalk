package com.freetalk.repository

import com.freetalk.data.entity.BoardEntity
import com.freetalk.data.entity.UserEntity
import com.freetalk.data.entity.toEntity
import com.freetalk.data.remote.*

interface BoardDataRepository {
    suspend fun insert(boardInsertForm: BoardInsetForm): BoardEntity
    //suspend fun select(): BoardSelectData
    suspend fun update(boardUpdateForm: BoardUpdateForm): BoardEntity
}

class FirebaseBoardDataRepositoryImpl(private val dataSource: BoardDataSource): BoardDataRepository{
    override suspend fun insert(boardInsertForm: BoardInsetForm): BoardEntity {
        return dataSource.insertContent(boardInsertForm).toEntity()
    }
/*
    override suspend fun select(): BoardSelectData {
        return dataSource.select()
    }

 */

    override suspend fun update(boardUpdateForm: BoardUpdateForm): BoardEntity {
        return dataSource.updateContent(boardUpdateForm).toEntity()
    }

}