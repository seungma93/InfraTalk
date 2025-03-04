package com.sm.infratalk.data.datasource.remote.board

import com.sm.infratalk.data.model.request.board.BoardDeleteRequest
import com.sm.infratalk.data.model.request.board.BoardInsertRequest
import com.sm.infratalk.data.model.request.board.BoardMetaListSelectRequest
import com.sm.infratalk.data.model.request.board.BoardSelectRequest
import com.sm.infratalk.data.model.request.board.BoardUpdateRequest
import com.sm.infratalk.data.model.request.board.MyBoardListLoadRequest
import com.sm.infratalk.data.model.response.board.BoardDeleteResponse
import com.sm.infratalk.data.model.response.board.BoardInsertResponse
import com.sm.infratalk.data.model.response.board.BoardMetaListResponse
import com.sm.infratalk.data.model.response.board.BoardMetaResponse

interface BoardDataSource {
    suspend fun insertBoard(boardInsertRequest: BoardInsertRequest): BoardInsertResponse
    suspend fun updateBoard(boardUpdateRequest: BoardUpdateRequest): BoardMetaResponse
    suspend fun selectBoard(boardSelectRequest: BoardSelectRequest): BoardMetaResponse
    suspend fun selectBoardMetaList(boardMetaListSelectRequest: BoardMetaListSelectRequest): BoardMetaListResponse
    suspend fun loadMyBoardList(myBoardListLoadRequest: MyBoardListLoadRequest): BoardMetaListResponse
    suspend fun deleteBoard(boardDeleteRequest: BoardDeleteRequest): BoardDeleteResponse
    suspend fun loadMyBookmarkBoardList(): BoardMetaListResponse
    suspend fun loadMyLikeBoardList(): BoardMetaListResponse
}