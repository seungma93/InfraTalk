package com.freetalk.presenter.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.freetalk.domain.entity.CommentListEntity
import com.freetalk.domain.entity.UserEntity
import com.freetalk.domain.usecase.AddCommentBookmarkUseCase
import com.freetalk.domain.usecase.AddCommentLikeUseCase
import com.freetalk.domain.usecase.DeleteCommentBookmarkUseCase
import com.freetalk.domain.usecase.DeleteCommentLikeUseCase
import com.freetalk.domain.usecase.DeleteCommentUseCase
import com.freetalk.domain.usecase.GetUserInfoUseCase
import com.freetalk.domain.usecase.LoadMyBookmarkCommentListUseCase
import com.freetalk.domain.usecase.LoadMyCommentListUseCase
import com.freetalk.presenter.form.CommentBookmarkAddForm
import com.freetalk.presenter.form.CommentBookmarkDeleteForm
import com.freetalk.presenter.form.CommentDeleteForm
import com.freetalk.presenter.form.CommentLikeAddForm
import com.freetalk.presenter.form.CommentLikeCountLoadForm
import com.freetalk.presenter.form.CommentLikeDeleteForm
import com.freetalk.presenter.form.CommentRelatedBookmarksDeleteForm
import com.freetalk.presenter.form.CommentRelatedLikesDeleteForm
import com.freetalk.presenter.form.MyCommentListLoadForm
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.updateAndGet
import javax.inject.Inject

class MyBookmarkCommentViewModel @Inject constructor(
    private val loadMyBookmarkCommentListUseCase: LoadMyBookmarkCommentListUseCase,
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

    suspend fun loadMyBookmarkCommentList(): MyCommentViewState {
        Log.d("comment", "뷰모델 시작")
        val result = kotlin.runCatching {
            loadMyBookmarkCommentListUseCase()
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