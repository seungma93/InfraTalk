package com.sm.infratalk.domain.board.repository

import com.sm.infratalk.domain.board.entity.BoardDeleteEntity
import com.sm.infratalk.domain.board.entity.BoardInsertEntity
import com.sm.infratalk.domain.board.entity.BoardMetaEntity
import com.sm.infratalk.domain.board.entity.BoardMetaListEntity
import com.sm.infratalk.presenter.board.form.BoardContentInsertForm
import com.sm.infratalk.presenter.board.form.BoardDeleteForm
import com.sm.infratalk.presenter.board.form.BoardListLoadForm
import com.sm.infratalk.presenter.board.form.BoardLoadForm
import com.sm.infratalk.presenter.board.form.BoardUpdateForm
import com.sm.infratalk.presenter.mypage.form.MyBoardListLoadForm

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