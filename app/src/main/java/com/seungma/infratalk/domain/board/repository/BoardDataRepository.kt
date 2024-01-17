package com.seungma.infratalk.domain.board.repository

import com.seungma.infratalk.domain.board.entity.BoardDeleteEntity
import com.seungma.infratalk.domain.board.entity.BoardInsertEntity
import com.seungma.infratalk.domain.board.entity.BoardMetaEntity
import com.seungma.infratalk.domain.board.entity.BoardMetaListEntity
import com.seungma.infratalk.presenter.board.form.BoardContentInsertForm
import com.seungma.infratalk.presenter.board.form.BoardDeleteForm
import com.seungma.infratalk.presenter.board.form.BoardListLoadForm
import com.seungma.infratalk.presenter.board.form.BoardLoadForm
import com.seungma.infratalk.presenter.board.form.BoardUpdateForm
import com.seungma.infratalk.presenter.mypage.form.MyBoardListLoadForm

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