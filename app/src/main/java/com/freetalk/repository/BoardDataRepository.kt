package com.freetalk.repository

import android.util.Log
import com.freetalk.data.entity.BoardEntity
import com.freetalk.data.entity.BoardListEntity
import com.freetalk.data.entity.UserEntity
import com.freetalk.data.entity.toEntity
import com.freetalk.data.remote.*
import com.google.firebase.firestore.DocumentSnapshot
import javax.inject.Inject

interface BoardDataRepository {
    suspend fun insert(boardInsertForm: BoardInsetForm): BoardEntity
    suspend fun select(boardSelectForm: BoardSelectForm): BoardListEntity
    suspend fun update(boardUpdateForm: BoardUpdateForm): BoardEntity
}

class BoardDataRepositoryImpl @Inject constructor(private val dataSource: BoardDataSource): BoardDataRepository{
    override suspend fun insert(boardInsertForm: BoardInsetForm): BoardEntity {
        return dataSource.insertContent(boardInsertForm).toEntity()
    }

    override suspend fun select(boardSelectForm: BoardSelectForm): BoardListEntity {
        Log.d("BoardDataRepository", "레포지토리")
        return dataSource.selectContents(boardSelectForm).toEntity()
    }

    override suspend fun update(boardUpdateForm: BoardUpdateForm): BoardEntity {
        return dataSource.updateContent(boardUpdateForm).toEntity()
    }

}