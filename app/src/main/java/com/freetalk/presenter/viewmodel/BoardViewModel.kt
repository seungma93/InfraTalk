package com.freetalk.presenter.viewmodel

import androidx.lifecycle.ViewModel
import com.freetalk.domain.entity.BoardEntity
import com.freetalk.domain.entity.BoardListEntity
import com.freetalk.domain.entity.BoardMetaEntity
import com.freetalk.domain.usecase.AddBoardBookmarkUseCase
import com.freetalk.domain.usecase.AddBoardLikeUseCase
import com.freetalk.domain.usecase.DeleteBoardBookmarkUseCase
import com.freetalk.domain.usecase.DeleteBoardLikeUseCase
import com.freetalk.domain.usecase.LoadBoardListUseCase
import com.freetalk.domain.usecase.UpdateImageContentUseCase
import com.freetalk.domain.usecase.WriteBoardContentUseCase
import com.freetalk.presenter.form.BoardBookmarkAddForm
import com.freetalk.presenter.form.BoardBookmarkDeleteForm
import com.freetalk.presenter.form.BoardContentImagesInsertForm
import com.freetalk.presenter.form.BoardContentInsertForm
import com.freetalk.presenter.form.BoardContentUpdateForm
import com.freetalk.presenter.form.BoardLikeAddForm
import com.freetalk.presenter.form.BoardLikeCountLoadForm
import com.freetalk.presenter.form.BoardLikeDeleteForm
import com.freetalk.presenter.form.BoardListLoadForm
import com.freetalk.presenter.form.BoardUpdateForm
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

sealed class BoardViewEvent {
    data class Register(val boardMetaEntity: BoardMetaEntity) : BoardViewEvent()
    data class Error(val errorCode: Throwable) : BoardViewEvent()
}

class BoardViewModel @Inject constructor(
    private val writeBoardContentUseCase: WriteBoardContentUseCase,
    private val updateImageContentUseCase: UpdateImageContentUseCase,
    private val loadBoardListUseCase: LoadBoardListUseCase,
    private val addBoardBookmarkUseCase: AddBoardBookmarkUseCase,
    private val deleteBoardBookmarkUseCase: DeleteBoardBookmarkUseCase,
    private val addBoardLikeUseCase: AddBoardLikeUseCase,
    private val deleteBoardLikeUseCase: DeleteBoardLikeUseCase
) : ViewModel() {
    private val _viewEvent = MutableSharedFlow<BoardViewEvent>()
    val viewEvent: SharedFlow<BoardViewEvent> = _viewEvent.asSharedFlow()

    private val _viewState =
        MutableStateFlow(BoardViewState(BoardListEntity(emptyList())))
    val viewState: StateFlow<BoardViewState> = _viewState.asStateFlow()

    data class BoardViewState(
        val boardListEntity: BoardListEntity
    )

    suspend fun writeBoardContent(
        boardContentInsertForm: BoardContentInsertForm,
        boardContentImagesInsertForm: BoardContentImagesInsertForm
    ) {
        kotlin.runCatching {
            val boardMetaEntity = writeBoardContentUseCase(boardContentInsertForm)
            val boardUpdateForm = BoardUpdateForm(
                author = boardMetaEntity.author,
                title = boardMetaEntity.title,
                content = boardMetaEntity.content,
                images = boardContentImagesInsertForm.images,
                createTime = boardMetaEntity.createTime
            )
            val boardEntity =
                updateImageContentUseCase.updateImageContent(boardUpdateForm = boardUpdateForm)
            _viewEvent.emit(BoardViewEvent.Register(boardMetaEntity))
        }.onFailure {
            _viewEvent.emit(BoardViewEvent.Error(it))
        }
    }

    suspend fun loadBoardList(boardListLoadForm: BoardListLoadForm): BoardViewState {
        val result = kotlin.runCatching {
            val boardListEntity = loadBoardListUseCase(boardListLoadForm = boardListLoadForm)
            when (boardListLoadForm.reload) {
                true -> boardListEntity.boardList
                false -> _viewState.value.boardListEntity.boardList + boardListEntity.boardList
            }
        }.onFailure {

        }.getOrNull()

        return result?.let {
            BoardViewState(boardListEntity = BoardListEntity(it)).apply {
                _viewState.value = this
            }
        } ?: viewState.value
    }

    suspend fun addLike(
        boardLikeAddForm: BoardLikeAddForm,
        boardLikeCountLoadForm: BoardLikeCountLoadForm
    ) {
        kotlin.runCatching {
            val boardListEntity = addBoardLikeUseCase(
                boardLikeAddForm = boardLikeAddForm,
                boardLikeCountLoadForm = boardLikeCountLoadForm,
                boardListEntity = BoardListEntity(boardList = _viewState.value.boardListEntity.boardList)
            )
            _viewState.value = BoardViewState(boardListEntity = boardListEntity)
        }.onFailure {

        }.getOrNull()
    }

    suspend fun deleteLike(
        boardLikeDeleteForm: BoardLikeDeleteForm,
        boardLikeCountLoadForm: BoardLikeCountLoadForm,
    ) {
        kotlin.runCatching {
            val boardListEntity = deleteBoardLikeUseCase(
                boardLikeDeleteForm = boardLikeDeleteForm,
                boardLikeCountLoadForm = boardLikeCountLoadForm,
                boardListEntity = BoardListEntity(boardList = _viewState.value.boardListEntity.boardList)
            )
            _viewState.value = BoardViewState(boardListEntity = boardListEntity)
        }.onFailure {

        }.getOrNull()
    }

    suspend fun addBookMark(
        boardBookmarkAddForm: BoardBookmarkAddForm
    ) {
        kotlin.runCatching {
            val boardListEntity = addBoardBookmarkUseCase(
                boardBookmarkAddForm = boardBookmarkAddForm,
                boardListEntity = BoardListEntity(boardList = _viewState.value.boardListEntity.boardList)
            )
            _viewState.value = BoardViewState(boardListEntity = boardListEntity)
        }.onFailure {

        }.getOrNull()
    }

    suspend fun deleteBookMark(
        boardBookmarkDeleteForm: BoardBookmarkDeleteForm
    ) {
        kotlin.runCatching {
            val boardListEntity = deleteBoardBookmarkUseCase(
                boardBookmarkDeleteForm = boardBookmarkDeleteForm,
                boardListEntity = BoardListEntity(boardList = _viewState.value.boardListEntity.boardList)
            )
            _viewState.value = BoardViewState(boardListEntity = boardListEntity)
        }.onFailure {

        }.getOrNull()
    }
}