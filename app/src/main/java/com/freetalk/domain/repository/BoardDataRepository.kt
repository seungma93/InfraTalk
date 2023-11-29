package com.freetalk.domain.repository

import com.freetalk.domain.entity.BoardDeleteEntity
import com.freetalk.domain.entity.BoardInsertEntity
import com.freetalk.domain.entity.BoardMetaEntity
import com.freetalk.domain.entity.BoardMetaListEntity
import com.freetalk.presenter.form.BoardContentInsertForm
import com.freetalk.presenter.form.BoardDeleteForm
import com.freetalk.presenter.form.BoardListLoadForm
import com.freetalk.presenter.form.BoardLoadForm
import com.freetalk.presenter.form.BoardUpdateForm
import com.freetalk.presenter.form.MyBoardListLoadForm

interface BoardDataRepository {
    suspend fun insertBoard(boardContentInsertForm: BoardContentInsertForm): BoardInsertEntity
    suspend fun loadBoardMetaList(boardListLoadForm: BoardListLoadForm): BoardMetaListEntity
    suspend fun updateBoard(boardUpdateForm: BoardUpdateForm): BoardMetaEntity
    suspend fun loadBoard(boardLoadForm: BoardLoadForm): BoardMetaEntity
    suspend fun loadMyBoardList(myBoardListLoadForm: MyBoardListLoadForm): BoardMetaListEntity
    suspend fun deleteBoard(boardDeleteForm: BoardDeleteForm): BoardDeleteEntity
    suspend fun loadMyBookmarkBoardList(): BoardMetaListEntity
    suspend fun loadMyLikeBoardList(): BoardMetaListEntity
}