package com.freetalk.domain.repository

import com.freetalk.data.UserSingleton
import com.freetalk.data.datasource.remote.BoardDataSource
import com.freetalk.data.mapper.toEntity
import com.freetalk.data.model.request.BoardDeleteRequest
import com.freetalk.data.model.request.BoardInsertRequest
import com.freetalk.data.model.request.BoardMetaListSelectRequest
import com.freetalk.data.model.request.BoardSelectRequest
import com.freetalk.data.model.request.BoardUpdateRequest
import com.freetalk.data.model.request.MyBoardListLoadRequest
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
import toEntity
import java.util.Date
import javax.inject.Inject

class BoardDataRepositoryImpl @Inject constructor(private val dataSource: BoardDataSource) :
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
                author = UserSingleton.userEntity,
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
}