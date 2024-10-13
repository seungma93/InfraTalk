package com.seungma.domain.repository

import com.seungma.infratalk.data.datasource.remote.board.BoardDataSource
import com.seungma.infratalk.data.mapper.toEntity
import com.seungma.infratalk.data.model.request.board.BoardDeleteRequest
import com.seungma.infratalk.data.model.request.board.BoardInsertRequest
import com.seungma.infratalk.data.model.request.board.BoardMetaListSelectRequest
import com.seungma.infratalk.data.model.request.board.BoardSelectRequest
import com.seungma.infratalk.data.model.request.board.BoardUpdateRequest
import com.seungma.infratalk.data.model.request.board.MyBoardListLoadRequest
import com.seungma.infratalk.domain.board.entity.BoardDeleteEntity
import com.seungma.infratalk.domain.board.entity.BoardInsertEntity
import com.seungma.infratalk.domain.board.entity.BoardMetaEntity
import com.seungma.infratalk.domain.board.entity.BoardMetaListEntity
import com.seungma.infratalk.domain.board.repository.BoardDataRepository
import com.seungma.infratalk.domain.user.repository.UserDataRepository
import com.seungma.infratalk.presenter.board.form.BoardContentInsertForm
import com.seungma.infratalk.presenter.board.form.BoardDeleteForm
import com.seungma.infratalk.presenter.board.form.BoardListLoadForm
import com.seungma.infratalk.presenter.board.form.BoardLoadForm
import com.seungma.infratalk.presenter.board.form.BoardUpdateForm
import com.seungma.infratalk.presenter.mypage.form.MyBoardListLoadForm
import java.util.Date
import javax.inject.Inject

class BoardDataRepositoryImpl @Inject constructor(private val dataSource: BoardDataSource, private val userDataRepository: UserDataRepository) :
    BoardDataRepository {
    override suspend fun insertBoard(boardContentInsertForm: BoardContentInsertForm): BoardInsertEntity =
        with(boardContentInsertForm) {

            return dataSource.insertBoard(
                boardInsertRequest = BoardInsertRequest(
                    authorEmail = author.email,
                    title = title,
                    content = content,
                    editTime = null
                )
            ).toEntity()
        }

    override suspend fun loadBoardMetaList(boardListLoadForm: BoardListLoadForm): BoardMetaListEntity {

        return dataSource.selectBoardMetaList(
            BoardMetaListSelectRequest(
                reload = boardListLoadForm.reload
            )
        ).toEntity()
    }

    override suspend fun updateBoard(boardUpdateForm: BoardUpdateForm): BoardMetaEntity {
        return dataSource.updateBoard(
            boardUpdateRequest = BoardUpdateRequest(
                author = userDataRepository.getUserMe(),
                title = boardUpdateForm.title,
                content = boardUpdateForm.content,
                images = boardUpdateForm.images,
                createTime = boardUpdateForm.createTime,
                editTime = Date()
            )
        ).toEntity()
    }

    override suspend fun loadBoard(boardLoadForm: BoardLoadForm): BoardMetaEntity {
        return dataSource.selectBoard(
            BoardSelectRequest(
                boardAuthorEmail = boardLoadForm.boardAuthorEmail,
                boardCreateTime = boardLoadForm.boardCreateTime
            )
        ).toEntity()
    }

    override suspend fun loadMyBoardList(myBoardListLoadForm: MyBoardListLoadForm): BoardMetaListEntity {
        return dataSource.loadMyBoardList(
            myBoardListLoadRequest = MyBoardListLoadRequest(
                reload = myBoardListLoadForm.reload
            )
        ).toEntity()
    }

    override suspend fun deleteBoard(boardDeleteForm: BoardDeleteForm): BoardDeleteEntity {
        return dataSource.deleteBoard(
            boardDeleteRequest = BoardDeleteRequest(
                boardAuthorEmail = boardDeleteForm.boardAuthorEmail,
                boardCreateTime = boardDeleteForm.boardCreateTime
            )
        ).toEntity()
    }

    override suspend fun loadMyBookmarkBoardList(): BoardMetaListEntity {
        return dataSource.loadMyBookmarkBoardList().toEntity()
    }

    override suspend fun loadMyLikeBoardList(): BoardMetaListEntity {
        return dataSource.loadMyLikeBoardList().toEntity()
    }
}