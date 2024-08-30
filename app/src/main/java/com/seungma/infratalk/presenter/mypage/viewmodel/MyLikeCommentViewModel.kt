package com.seungma.infratalk.presenter.mypage.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.seungma.infratalk.domain.comment.entity.CommentListEntity
import com.seungma.infratalk.domain.comment.usecase.AddCommentBookmarkUseCase
import com.seungma.infratalk.domain.comment.usecase.AddCommentLikeUseCase
import com.seungma.infratalk.domain.comment.usecase.DeleteCommentBookmarkUseCase
import com.seungma.infratalk.domain.comment.usecase.DeleteCommentLikeUseCase
import com.seungma.infratalk.domain.comment.usecase.DeleteCommentUseCase
import com.seungma.infratalk.domain.mypage.usecase.LoadMyLikeCommentListUseCase
import com.seungma.infratalk.domain.user.usecase.GetUserInfoUseCase
import com.seungma.infratalk.domain.user.entity.UserEntity
import com.seungma.infratalk.presenter.board.form.CommentBookmarkAddForm
import com.seungma.infratalk.presenter.board.form.CommentBookmarkDeleteForm
import com.seungma.infratalk.presenter.board.form.CommentDeleteForm
import com.seungma.infratalk.presenter.board.form.CommentLikeAddForm
import com.seungma.infratalk.presenter.board.form.CommentLikeCountLoadForm
import com.seungma.infratalk.presenter.board.form.CommentLikeDeleteForm
import com.seungma.infratalk.presenter.board.form.CommentRelatedBookmarksDeleteForm
import com.seungma.infratalk.presenter.board.form.CommentRelatedLikesDeleteForm
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.updateAndGet
import javax.inject.Inject

class MyLikeCommentViewModel @Inject constructor(
    private val loadMyLikeCommentListUseCase: LoadMyLikeCommentListUseCase,
    private val deleteCommentUseCase: DeleteCommentUseCase,
    private val addCommentBookmarkUseCase: AddCommentBookmarkUseCase,
    private val deleteCommentBookmarkUseCase: DeleteCommentBookmarkUseCase,
    private val addCommentLikeUseCase: AddCommentLikeUseCase,
    private val deleteCommentLikeUseCase: DeleteCommentLikeUseCase,
    private val getUserInfoUseCase: GetUserInfoUseCase
) : ViewModel() {
    private val _viewState = MutableStateFlow<MyCommentViewState>(
        MyCommentViewState(
            commentListEntity = null
        )
    )
    private val viewState: StateFlow<MyCommentViewState> = _viewState.asStateFlow()

    data class MyCommentViewState(
        val commentListEntity: CommentListEntity?
    )

    suspend fun loadMyLikeCommentList(): MyCommentViewState {
        Log.d("comment", "뷰모델 시작")
        val result = kotlin.runCatching {
            loadMyLikeCommentListUseCase()
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
    ): MyCommentViewState {
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
    ): MyCommentViewState {
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
    ): MyCommentViewState {
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
    ): MyCommentViewState {
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
    ): MyCommentViewState {
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


    fun getUserInfo(): UserEntity {
        return getUserInfoUseCase()
    }
}