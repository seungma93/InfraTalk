package com.freetalk.presenter.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.freetalk.domain.entity.BoardListEntity
import com.freetalk.domain.entity.BoardWriteEntity
import com.freetalk.domain.usecase.AddBoardBookmarkUseCase
import com.freetalk.domain.usecase.AddBoardLikeUseCase
import com.freetalk.domain.usecase.DeleteBoardBookmarkUseCase
import com.freetalk.domain.usecase.DeleteBoardLikeUseCase
import com.freetalk.domain.usecase.LoadBoardListUseCase
import com.freetalk.domain.usecase.UpdateBoardContentImagesUseCase
import com.freetalk.domain.usecase.WriteBoardContentUseCase
import com.freetalk.presenter.form.BoardBookmarkAddForm
import com.freetalk.presenter.form.BoardBookmarkDeleteForm
import com.freetalk.presenter.form.BoardContentImagesUpdateForm
import com.freetalk.presenter.form.BoardContentInsertForm
import com.freetalk.presenter.form.BoardLikeAddForm
import com.freetalk.presenter.form.BoardLikeCountLoadForm
import com.freetalk.presenter.form.BoardLikeDeleteForm
import com.freetalk.presenter.form.BoardListLoadForm
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

sealed class BoardViewEvent {
    data class Register(val boardWriteEntity: BoardWriteEntity) : BoardViewEvent()
    data class Error(val errorCode: Throwable) : BoardViewEvent()
}

class BoardViewModel @Inject constructor(
    private val writeBoardContentUseCase: WriteBoardContentUseCase,
    private val updateBoardContentImagesUseCase: UpdateBoardContentImagesUseCase,
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
        boardContentInsertForm: BoardContentInsertForm
    ) {
        kotlin.runCatching {
            val boardInsertEntity =
                writeBoardContentUseCase(boardContentInsertForm = boardContentInsertForm)

            when (boardContentInsertForm.images.isNullOrEmpty()) {
                true -> {
                    _viewEvent.emit(
                        BoardViewEvent.Register(
                            boardWriteEntity = BoardWriteEntity(
                                isSuccess = true
                            )
                        )
                    )
                }

                false -> {
                    updateBoardContentImagesUseCase(
                        boardContentImagesUpdateForm = BoardContentImagesUpdateForm(
                            boardAuthorEmail = boardInsertEntity.boardAuthorEmail,
                            boardCreateTime = boardInsertEntity.boardCreteTime,
                            images = boardContentInsertForm.images
                        )
                    )
                    _viewEvent.emit(
                        BoardViewEvent.Register(
                            boardWriteEntity = BoardWriteEntity(
                                isSuccess = true
                            )
                        )
                    )
                }
            }

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
    ): BoardViewState {
        val result = kotlin.runCatching {
            addBoardLikeUseCase(
                boardLikeAddForm = boardLikeAddForm,
                boardLikeCountLoadForm = boardLikeCountLoadForm,
                boardListEntity = BoardListEntity(boardList = viewState.value.boardListEntity.boardList)
            )
        }.onFailure {

        }.getOrNull()

        return result?.let {
            BoardViewState(boardListEntity = it).apply {
                _viewState.value = this
            }
        } ?: viewState.value
    }

    suspend fun deleteLike(
        boardLikeDeleteForm: BoardLikeDeleteForm,
        boardLikeCountLoadForm: BoardLikeCountLoadForm,
    ): BoardViewState {
        val result = kotlin.runCatching {
            deleteBoardLikeUseCase(
                boardLikeDeleteForm = boardLikeDeleteForm,
                boardLikeCountLoadForm = boardLikeCountLoadForm,
                boardListEntity = BoardListEntity(boardList = viewState.value.boardListEntity.boardList)
            )
        }.onFailure {

        }.getOrNull()

        return result?.let {
            BoardViewState(boardListEntity = it).apply {
                _viewState.value = this
            }
        } ?: viewState.value
    }

    suspend fun addBookMark(
        boardBookmarkAddForm: BoardBookmarkAddForm
    ):BoardViewState {
        val result = kotlin.runCatching {
            addBoardBookmarkUseCase(
                boardBookmarkAddForm = boardBookmarkAddForm,
                boardListEntity = BoardListEntity(boardList = _viewState.value.boardListEntity.boardList)
            )
        }.onFailure {

        }.getOrNull()

        return result?.let {
            BoardViewState(boardListEntity = it).apply {
                _viewState.value = this
            }
        } ?: viewState.value
    }

    suspend fun deleteBookMark(
        boardBookmarkDeleteForm: BoardBookmarkDeleteForm
    ):BoardViewState {
        val result = kotlin.runCatching {
            deleteBoardBookmarkUseCase(
                boardBookmarkDeleteForm = boardBookmarkDeleteForm,
                boardListEntity = BoardListEntity(boardList = _viewState.value.boardListEntity.boardList)
            )
        }.onFailure {

        }.getOrNull()
        return result?.let {
            BoardViewState(boardListEntity = it).apply {
                _viewState.value = this
            }
        } ?: viewState.value
    }
}