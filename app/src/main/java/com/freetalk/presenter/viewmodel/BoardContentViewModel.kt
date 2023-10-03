package com.freetalk.presenter.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.freetalk.domain.entity.BoardEntity
import com.freetalk.domain.entity.BoardMetaEntity
import com.freetalk.domain.entity.BookmarkEntity
import com.freetalk.domain.entity.CommentEntity
import com.freetalk.domain.entity.CommentListEntity
import com.freetalk.domain.entity.CommentMetaEntity
import com.freetalk.domain.entity.LikeCountEntity
import com.freetalk.domain.entity.LikeEntity
import com.freetalk.domain.entity.UserEntity
import com.freetalk.domain.usecase.AddBoardContentBookmarkUseCase
import com.freetalk.domain.usecase.AddBoardContentLikeUseCase
import com.freetalk.domain.usecase.AddCommentBookmarkUseCase
import com.freetalk.domain.usecase.AddCommentLikeUseCase
import com.freetalk.domain.usecase.DeleteBoardContentBookmarkUseCase
import com.freetalk.domain.usecase.DeleteBoardContentLikeUseCase
import com.freetalk.domain.usecase.DeleteCommentBookmarkUseCase
import com.freetalk.domain.usecase.DeleteCommentLikeUseCase
import com.freetalk.domain.usecase.DeleteCommentUseCase
import com.freetalk.domain.usecase.GetUserInfoUseCase
import com.freetalk.domain.usecase.LoadBoardContentUseCase
import com.freetalk.domain.usecase.LoadBoardRelatedAllCommentListUseCase
import com.freetalk.domain.usecase.LoadCommentListUseCase
import com.freetalk.domain.usecase.WriteCommentUseCase
import com.freetalk.presenter.form.BoardBookmarkAddForm
import com.freetalk.presenter.form.BoardBookmarkDeleteForm
import com.freetalk.presenter.form.BoardBookmarkLoadForm
import com.freetalk.presenter.form.BoardLikeAddForm
import com.freetalk.presenter.form.BoardLikeCountLoadForm
import com.freetalk.presenter.form.BoardLikeDeleteForm
import com.freetalk.presenter.form.BoardLikeLoadForm
import com.freetalk.presenter.form.BoardLoadForm
import com.freetalk.presenter.form.BoardRelatedAllCommentMetaListSelectForm
import com.freetalk.presenter.form.CommentBookmarkAddForm
import com.freetalk.presenter.form.CommentBookmarkDeleteForm
import com.freetalk.presenter.form.CommentInsertForm
import com.freetalk.presenter.form.CommentLikeAddForm
import com.freetalk.presenter.form.CommentLikeCountLoadForm
import com.freetalk.presenter.form.CommentLikeDeleteForm
import com.freetalk.presenter.form.CommentMetaListLoadForm
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.updateAndGet
import javax.inject.Inject

sealed class BoardContentViewEvent {
    data class RegisterComment(val commentMetaEntity: CommentMetaEntity) : BoardContentViewEvent()
    data class Error(val errorCode: Throwable) : BoardContentViewEvent()
}

class BoardContentViewModel @Inject constructor(
    private val writeCommentUseCase: WriteCommentUseCase,
    private val loadBoardContentUseCase: LoadBoardContentUseCase,
    private val loadCommentListUseCase: LoadCommentListUseCase,
    private val loadBoardRelatedAllCommentListUseCase: LoadBoardRelatedAllCommentListUseCase,
    private val deleteCommentUseCase: DeleteCommentUseCase,
    private val addBoardContentBookmarkUseCase: AddBoardContentBookmarkUseCase,
    private val deleteBoardContentBookmarkUseCase: DeleteBoardContentBookmarkUseCase,
    private val addBoardContentLikeUseCase: AddBoardContentLikeUseCase,
    private val deleteBoardContentLikeUseCase: DeleteBoardContentLikeUseCase,
    private val addCommentBookmarkUseCase: AddCommentBookmarkUseCase,
    private val deleteCommentBookmarkUseCase: DeleteCommentBookmarkUseCase,
    private val addCommentLikeUseCase: AddCommentLikeUseCase,
    private val deleteCommentLikeUseCase: DeleteCommentLikeUseCase,
    private val getUserInfoUseCase: GetUserInfoUseCase
) : ViewModel() {
    private val _viewEvent = MutableSharedFlow<BoardContentViewEvent>()
    private val viewEvent: SharedFlow<BoardContentViewEvent> = _viewEvent.asSharedFlow()

    private val _viewState = MutableStateFlow<BoardContentViewState>(
        BoardContentViewState(
            boardEntity = null,
            commentListEntity = null
        )
    )
    private val viewState: StateFlow<BoardContentViewState> = _viewState.asStateFlow()

    data class BoardContentViewState(
        val boardEntity: BoardEntity?,
        val commentListEntity: CommentListEntity?
    )

    suspend fun loadBoardContent(
        boardLoadForm: BoardLoadForm,
        boardBookmarkLoadForm: BoardBookmarkLoadForm,
        boardLikeLoadForm: BoardLikeLoadForm,
        boardLikeCountLoadForm: BoardLikeCountLoadForm
    ): BoardContentViewState {
        val result = kotlin.runCatching {
            loadBoardContentUseCase.invoke(
                boardLoadForm = boardLoadForm,
                boardBookmarkLoadForm = boardBookmarkLoadForm,
                boardLikeLoadForm = boardLikeLoadForm,
                boardLikeCountLoadForm = boardLikeCountLoadForm
            )
        }.onFailure {

        }.getOrNull()

        return result?.let {
            _viewState.updateAndGet { _ ->
                viewState.value.copy(boardEntity = it)
            }
        } ?: viewState.value
    }

    suspend fun addBoardContentBookmark(
        boardBookmarkAddForm: BoardBookmarkAddForm
    ): BoardContentViewState {
        val result = kotlin.runCatching {
            addBoardContentBookmarkUseCase(
                boardBookmarkAddForm = boardBookmarkAddForm,
                boardEntity = viewState.value.boardEntity ?: error("")
            )
        }.onFailure {

        }.getOrNull()
        return result?.let {
            _viewState.updateAndGet { _ ->
                viewState.value.copy(boardEntity = it)
            }
        } ?: viewState.value
    }

    suspend fun deleteBoardContentBookmark(
        boardBookmarkDeleteForm: BoardBookmarkDeleteForm
    ): BoardContentViewState {
        val result = kotlin.runCatching {
            deleteBoardContentBookmarkUseCase(
                boardBookmarkDeleteForm = boardBookmarkDeleteForm,
                boardEntity = viewState.value.boardEntity ?: error("")
            )
        }.onFailure {

        }.getOrNull()
        return result?.let {
            _viewState.updateAndGet { _ ->
                viewState.value.copy(boardEntity = it)
            }
        } ?: viewState.value
    }

    suspend fun addBoardContentLike(
        boardLikeAddForm: BoardLikeAddForm,
        boardLikeCountLoadForm: BoardLikeCountLoadForm,
    ): BoardContentViewState {
        val result = kotlin.runCatching {
            addBoardContentLikeUseCase(
                boardLikeAddForm = boardLikeAddForm,
                boardLikeCountLoadForm = boardLikeCountLoadForm,
                boardEntity = viewState.value.boardEntity ?: error("")
            )
        }.onFailure {

        }.getOrNull()
        return result?.let {
            _viewState.updateAndGet { _ ->
                viewState.value.copy(boardEntity = it)
            }
        } ?: viewState.value
    }

    suspend fun deleteBoardContentLike(
        boardLikeDeleteForm: BoardLikeDeleteForm,
        boardLikeCountLoadForm: BoardLikeCountLoadForm
    ): BoardContentViewState {
        val result = kotlin.runCatching {
            deleteBoardContentLikeUseCase(
                boardLikeDeleteForm = boardLikeDeleteForm,
                boardLikeCountLoadForm = boardLikeCountLoadForm,
                boardEntity = viewState.value.boardEntity ?: error("")
            )

        }.onFailure {

        }.getOrNull()
        return result?.let {
            _viewState.updateAndGet { _ ->
                viewState.value.copy(boardEntity = it)
            }
        } ?: viewState.value
    }

    suspend fun writeComment(
        commentInsertForm: CommentInsertForm
    ) {
        kotlin.runCatching {
            val commentMetaEntity = writeCommentUseCase(commentInsertForm = commentInsertForm)
            _viewEvent.emit(BoardContentViewEvent.RegisterComment(commentMetaEntity))
        }.onFailure {
            _viewEvent.emit(BoardContentViewEvent.Error(it))
        }.getOrNull()
    }

    suspend fun loadCommentList(commentMetaListLoadForm: CommentMetaListLoadForm): BoardContentViewState {
        Log.d("comment", "뷰모델 시작")
        val result = kotlin.runCatching {
            val commentListEntity =
                loadCommentListUseCase(commentMetaListLoadForm = commentMetaListLoadForm)

            when (commentMetaListLoadForm.reload) {
                true -> commentListEntity.commentList
                false -> viewState.value.commentListEntity?.let { it.commentList + commentListEntity.commentList }
                    ?: run { commentListEntity.commentList }
            }
        }.onFailure {

        }.getOrNull()

        return result?.let {
            _viewState.updateAndGet { _ ->
                viewState.value.copy(commentListEntity = CommentListEntity(it))
            }
        } ?: viewState.value
    }

    suspend fun loadBoardRelatedAllCommentList(
        boardRelatedAllCommentMetaListSelectForm: BoardRelatedAllCommentMetaListSelectForm
    ): BoardContentViewState {
        val result = kotlin.runCatching {
            loadBoardRelatedAllCommentListUseCase(
                boardRelatedAllCommentMetaListSelectForm = boardRelatedAllCommentMetaListSelectForm
            )
        }.onFailure {

        }.getOrNull()

        return result?.let {
            _viewState.updateAndGet { _ ->
                viewState.value.copy(commentListEntity = it)
            }
        } ?: viewState.value
    }

    suspend fun addCommentLike(
        commentLikeAddForm: CommentLikeAddForm,
        commentLikeCountLoadForm: CommentLikeCountLoadForm
    ): BoardContentViewState {
        val result = kotlin.runCatching {
            addCommentLikeUseCase(
                commentLikeAddForm = commentLikeAddForm,
                commentLikeCountLoadForm = commentLikeCountLoadForm,
                commentListEntity = viewState.value.commentListEntity ?: error("")
            )
        }.onFailure {

        }.getOrNull()
        return result?.let {
            _viewState.updateAndGet { _ ->
                viewState.value.copy(commentListEntity = it)
            }
        } ?: viewState.value
    }

    suspend fun deleteCommentLike(
        commentLikeDeleteForm: CommentLikeDeleteForm,
        commentLikeCountLoadForm: CommentLikeCountLoadForm
    ): BoardContentViewState {
        val result = kotlin.runCatching {
            deleteCommentLikeUseCase(
                commentLikeDeleteForm = commentLikeDeleteForm,
                commentLikeCountLoadForm = commentLikeCountLoadForm,
                commentListEntity = viewState.value.commentListEntity ?: error("")
            )

        }.onFailure {

        }.getOrNull()
        return result?.let {
            _viewState.updateAndGet { _ ->
                viewState.value.copy(commentListEntity = it)
            }
        } ?: viewState.value
    }

    suspend fun addCommentBookmark(
        commentBookmarkAddForm: CommentBookmarkAddForm
    ): BoardContentViewState {
        val result = kotlin.runCatching {
            addCommentBookmarkUseCase(
                commentBookmarkAddForm = commentBookmarkAddForm,
                commentListEntity = viewState.value.commentListEntity ?: error("")
            )
        }.onFailure {

        }.getOrNull()
        return result?.let {
            _viewState.updateAndGet { _ ->
                viewState.value.copy(commentListEntity = it)
            }
        } ?: viewState.value
    }

    suspend fun deleteCommentBookmark(
        commentBookmarkDeleteForm: CommentBookmarkDeleteForm
    ): BoardContentViewState {
        val result = kotlin.runCatching {
            deleteCommentBookmarkUseCase(
                commentBookmarkDeleteForm = commentBookmarkDeleteForm,
                commentListEntity = viewState.value.commentListEntity ?: error("")
            )

        }.onFailure {

        }.getOrNull()
        return result?.let {
            _viewState.updateAndGet { _ ->
                viewState.value.copy(commentListEntity = it)
            }
        } ?: viewState.value
    }
    /*
        suspend fun deleteComment(

        ) {
            kotlin.runCatching {
                val newList = deleteCommentUseCase(
                    deleteCommentForm,
                    _viewState.value.commentList
                )
                _viewState.value =
                    BoardContentViewState(
                        _viewState.value.wrapperBoardEntity,
                        PrintCommentListUseCase.WrapperCommentList(newList)
                    )
            }.onFailure {
                Log.d("BoardViewModel", "북마크 딜리트 실패")
            }.getOrNull()
        }

     */

    fun getUserInfo(): UserEntity {
        return getUserInfoUseCase()
    }
}