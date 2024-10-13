package com.seungma.infratalk.presenter.board.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.seungma.infratalk.domain.board.entity.BoardEntity
import com.seungma.infratalk.domain.board.usecase.AddBoardContentBookmarkUseCase
import com.seungma.infratalk.domain.board.usecase.AddBoardContentLikeUseCase
import com.seungma.infratalk.domain.board.usecase.DeleteBoardContentBookmarkUseCase
import com.seungma.infratalk.domain.board.usecase.DeleteBoardContentLikeUseCase
import com.seungma.infratalk.domain.board.usecase.LoadBoardContentUseCase
import com.seungma.infratalk.domain.comment.entity.CommentListEntity
import com.seungma.infratalk.domain.comment.entity.CommentMetaEntity
import com.seungma.infratalk.domain.comment.usecase.AddCommentBookmarkUseCase
import com.seungma.infratalk.domain.comment.usecase.AddCommentLikeUseCase
import com.seungma.infratalk.domain.comment.usecase.DeleteCommentBookmarkUseCase
import com.seungma.infratalk.domain.comment.usecase.DeleteCommentLikeUseCase
import com.seungma.infratalk.domain.comment.usecase.DeleteCommentUseCase
import com.seungma.infratalk.domain.comment.usecase.LoadBoardRelatedAllCommentListUseCase
import com.seungma.infratalk.domain.comment.usecase.LoadCommentListUseCase
import com.seungma.infratalk.domain.comment.usecase.WriteCommentUseCase
import com.seungma.infratalk.domain.user.usecase.GetUserMeUseCase
import com.seungma.infratalk.domain.user.entity.UserEntity
import com.seungma.infratalk.presenter.board.form.BoardBookmarkAddForm
import com.seungma.infratalk.presenter.board.form.BoardBookmarkDeleteForm
import com.seungma.infratalk.presenter.board.form.BoardBookmarkLoadForm
import com.seungma.infratalk.presenter.board.form.BoardLikeAddForm
import com.seungma.infratalk.presenter.board.form.BoardLikeCountLoadForm
import com.seungma.infratalk.presenter.board.form.BoardLikeDeleteForm
import com.seungma.infratalk.presenter.board.form.BoardLikeLoadForm
import com.seungma.infratalk.presenter.board.form.BoardLoadForm
import com.seungma.infratalk.presenter.board.form.BoardRelatedAllCommentMetaListSelectForm
import com.seungma.infratalk.presenter.board.form.CommentBookmarkAddForm
import com.seungma.infratalk.presenter.board.form.CommentBookmarkDeleteForm
import com.seungma.infratalk.presenter.board.form.CommentDeleteForm
import com.seungma.infratalk.presenter.board.form.CommentInsertForm
import com.seungma.infratalk.presenter.board.form.CommentLikeAddForm
import com.seungma.infratalk.presenter.board.form.CommentLikeCountLoadForm
import com.seungma.infratalk.presenter.board.form.CommentLikeDeleteForm
import com.seungma.infratalk.presenter.board.form.CommentMetaListLoadForm
import com.seungma.infratalk.presenter.board.form.CommentRelatedBookmarksDeleteForm
import com.seungma.infratalk.presenter.board.form.CommentRelatedLikesDeleteForm
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
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
    private val getUserMeUseCase: GetUserMeUseCase
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

    suspend fun loadBoardAndComment(
        boardLoadForm: BoardLoadForm,
        boardBookmarkLoadForm: BoardBookmarkLoadForm,
        boardLikeLoadForm: BoardLikeLoadForm,
        boardLikeCountLoadForm: BoardLikeCountLoadForm,
        commentMetaListLoadForm: CommentMetaListLoadForm
    ): BoardContentViewState = coroutineScope {
        val result = kotlin.runCatching {

            val asyncBoard = async {
                loadBoardContentUseCase(
                    boardLoadForm = boardLoadForm,
                    boardBookmarkLoadForm = boardBookmarkLoadForm,
                    boardLikeLoadForm = boardLikeLoadForm,
                    boardLikeCountLoadForm = boardLikeCountLoadForm
                )
            }

            val asyncComment =
                async { loadCommentListUseCase(commentMetaListLoadForm = commentMetaListLoadForm) }

            val boardEntity = asyncBoard.await()
            val commentListEntity = asyncComment.await()

            val commentList = when (commentMetaListLoadForm.reload) {
                true -> commentListEntity.commentList
                false -> viewState.value.commentListEntity?.let { it.commentList + commentListEntity.commentList }
                    ?: run { commentListEntity.commentList }
            }
            Pair(boardEntity, commentList)
        }.onFailure {

        }.getOrNull()

        result?.let {
            val boardEntity = it.first
            val commentList = it.second
            _viewState.updateAndGet { _ ->
                viewState.value.copy(
                    boardEntity = boardEntity,
                    commentListEntity = CommentListEntity(commentList = commentList)
                )
            }
        } ?: viewState.value
    }

    suspend fun loadBoardContent(
        boardLoadForm: BoardLoadForm,
        boardBookmarkLoadForm: BoardBookmarkLoadForm,
        boardLikeLoadForm: BoardLikeLoadForm,
        boardLikeCountLoadForm: BoardLikeCountLoadForm
    ): BoardContentViewState {
        val result = kotlin.runCatching {
            loadBoardContentUseCase(
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

    suspend fun deleteComment(
        commentDeleteForm: CommentDeleteForm,
        commentRelatedBookmarksDeleteForm: CommentRelatedBookmarksDeleteForm,
        commentRelatedLikesDeleteForm: CommentRelatedLikesDeleteForm
    ): BoardContentViewState {
        val result = kotlin.runCatching {
            deleteCommentUseCase(
                commentDeleteForm = commentDeleteForm,
                commentRelatedBookmarksDeleteForm = commentRelatedBookmarksDeleteForm,
                commentRelatedLikesDeleteForm = commentRelatedLikesDeleteForm,
                commentListEntity = viewState.value.commentListEntity ?: error("")
            )

        }.onFailure {
            Log.d("BoardViewModel", "북마크 딜리트 실패")
        }.getOrNull()

        return result?.let {
            _viewState.updateAndGet { _ ->
                viewState.value.copy(commentListEntity = it)
            }
        } ?: viewState.value
    }


    suspend fun getUserMe(): UserEntity {
        return getUserMeUseCase()
    }
}