package com.seungma.infratalk.presenter.mypage.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.seungma.infratalk.domain.board.entity.BoardListEntity
import com.seungma.infratalk.domain.board.usecase.AddBoardBookmarkUseCase
import com.seungma.infratalk.domain.board.usecase.AddBoardLikeUseCase
import com.seungma.infratalk.domain.board.usecase.DeleteBoardBookmarkUseCase
import com.seungma.infratalk.domain.board.usecase.DeleteBoardLikeUseCase
import com.seungma.infratalk.domain.board.usecase.DeleteBoardUseCase
import com.seungma.infratalk.domain.mypage.usecase.LoadMyBoardListUseCase
import com.seungma.infratalk.domain.user.entity.UserEntity
import com.seungma.infratalk.domain.user.usecase.GetUserMeUseCase
import com.seungma.infratalk.presenter.board.form.BoardBookmarkAddForm
import com.seungma.infratalk.presenter.board.form.BoardBookmarkDeleteForm
import com.seungma.infratalk.presenter.board.form.BoardBookmarksDeleteForm
import com.seungma.infratalk.presenter.board.form.BoardDeleteForm
import com.seungma.infratalk.presenter.board.form.BoardLikeAddForm
import com.seungma.infratalk.presenter.board.form.BoardLikeCountLoadForm
import com.seungma.infratalk.presenter.board.form.BoardLikeDeleteForm
import com.seungma.infratalk.presenter.board.form.BoardLikesDeleteForm
import com.seungma.infratalk.presenter.mypage.form.MyBoardListLoadForm
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.updateAndGet
import javax.inject.Inject

class MyBoardViewModel @Inject constructor(
    private val loadMyBoardListUseCase: LoadMyBoardListUseCase,
    private val addBoardBookmarkUseCase: AddBoardBookmarkUseCase,
    private val deleteBoardBookmarkUseCase: DeleteBoardBookmarkUseCase,
    private val addBoardLikeUseCase: AddBoardLikeUseCase,
    private val deleteBoardLikeUseCase: DeleteBoardLikeUseCase,
    private val getUserMeUseCase: GetUserMeUseCase,
    private val deleteBoardUseCase: DeleteBoardUseCase
) : ViewModel() {
    private val _viewState =
        MutableStateFlow(MyBoardViewState(BoardListEntity(emptyList())))
    private val viewState: StateFlow<MyBoardViewState> = _viewState.asStateFlow()

    data class MyBoardViewState(
        val boardListEntity: BoardListEntity
    )


    suspend fun loadMyBoardList(myBoardListLoadForm: MyBoardListLoadForm): MyBoardViewState {
        val result = kotlin.runCatching {
            val boardListEntity = loadMyBoardListUseCase(myBoardListLoadForm = myBoardListLoadForm)
            when (myBoardListLoadForm.reload) {
                true -> boardListEntity.boardList
                false -> _viewState.value.boardListEntity.boardList + boardListEntity.boardList
            }
        }.onFailure {

        }.getOrNull()

        return result?.let {
            MyBoardViewState(boardListEntity = BoardListEntity(it)).apply {
                _viewState.value = this
            }
        } ?: viewState.value
    }

    suspend fun addLike(
        boardLikeAddForm: BoardLikeAddForm,
        boardLikeCountLoadForm: BoardLikeCountLoadForm
    ): MyBoardViewState {
        val result = kotlin.runCatching {
            addBoardLikeUseCase(
                boardLikeAddForm = boardLikeAddForm,
                boardLikeCountLoadForm = boardLikeCountLoadForm,
                boardListEntity = BoardListEntity(boardList = viewState.value.boardListEntity.boardList)
            )
        }.onFailure {

        }.getOrNull()

        return result?.let {
            MyBoardViewState(boardListEntity = it).apply {
                _viewState.value = this
            }
        } ?: viewState.value
    }

    suspend fun deleteLike(
        boardLikeDeleteForm: BoardLikeDeleteForm,
        boardLikeCountLoadForm: BoardLikeCountLoadForm,
    ): MyBoardViewState {
        val result = kotlin.runCatching {
            deleteBoardLikeUseCase(
                boardLikeDeleteForm = boardLikeDeleteForm,
                boardLikeCountLoadForm = boardLikeCountLoadForm,
                boardListEntity = BoardListEntity(boardList = viewState.value.boardListEntity.boardList)
            )
        }.onFailure {

        }.getOrNull()

        return result?.let {
            MyBoardViewState(boardListEntity = it).apply {
                _viewState.value = this
            }
        } ?: viewState.value
    }

    suspend fun addBookMark(
        boardBookmarkAddForm: BoardBookmarkAddForm
    ): MyBoardViewState {
        val result = kotlin.runCatching {
            addBoardBookmarkUseCase(
                boardBookmarkAddForm = boardBookmarkAddForm,
                boardListEntity = BoardListEntity(boardList = _viewState.value.boardListEntity.boardList)
            )
        }.onFailure {

        }.getOrNull()

        return result?.let {
            MyBoardViewState(boardListEntity = it).apply {
                _viewState.value = this
            }
        } ?: viewState.value
    }

    suspend fun deleteBookMark(
        boardBookmarkDeleteForm: BoardBookmarkDeleteForm
    ): MyBoardViewState {
        val result = kotlin.runCatching {
            deleteBoardBookmarkUseCase(
                boardBookmarkDeleteForm = boardBookmarkDeleteForm,
                boardListEntity = BoardListEntity(boardList = _viewState.value.boardListEntity.boardList)
            )
        }.onFailure {

        }.getOrNull()
        return result?.let {
            MyBoardViewState(boardListEntity = it).apply {
                _viewState.value = this
            }
        } ?: viewState.value
    }

    suspend fun getUserMe(): UserEntity {
        return getUserMeUseCase()
    }

    suspend fun deleteBoard(
        boardDeleteForm: BoardDeleteForm,
        boardBookmarksDeleteForm: BoardBookmarksDeleteForm,
        boardLikesDeleteForm: BoardLikesDeleteForm
    ): MyBoardViewState {
        val result = kotlin.runCatching {
            deleteBoardUseCase(
                boardDeleteForm = boardDeleteForm,
                boardBookmarksDeleteForm = boardBookmarksDeleteForm,
                boardLikesDeleteForm = boardLikesDeleteForm,
                boardListEntity = viewState.value.boardListEntity
            )

        }.onFailure {
            Log.d("BoardViewModel", "북마크 딜리트 실패")
        }.getOrNull()

        return result?.let {
            _viewState.updateAndGet { _ ->
                viewState.value.copy(boardListEntity = it)
            }
        } ?: viewState.value
    }

}