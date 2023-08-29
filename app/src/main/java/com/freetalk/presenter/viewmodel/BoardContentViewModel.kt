package com.freetalk.presenter.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.freetalk.data.entity.*
import com.freetalk.data.remote.*
import com.freetalk.usecase.*
import kotlinx.coroutines.flow.*
import javax.inject.Inject


class BoardContentViewModel @Inject constructor(
    private val insertBookMarkBoardContentUseCase: InsertBookMarkBoardContentUseCase,
    private val deleteBookMarkBoardContentUseCase: DeleteBookMarkBoardContentUseCase,
    private val selectBoardContentUseCase: SelectBoardContentUseCase,
    private val insertLikeBoardContentUseCase: InsertLikeBoardContentUseCase,
    private val deleteLikeBoardContentUseCase: DeleteLikeBoardContentUseCase,
    private val writeCommentUseCase: WriteCommentUseCase
) : ViewModel() {

    // 처음 값 생성
    private val boardContentViewState = BoardContentViewState(WrapperBoardEntity(), CommentEntity())
    private val _viewState = MutableStateFlow<BoardContentViewState>(boardContentViewState)
    val viewState: StateFlow<BoardContentViewState> = _viewState.asStateFlow()

    data class BoardContentViewState(
        val wrapperBoardEntity: WrapperBoardEntity,
        val commentEntity: CommentEntity
    )

    suspend fun select(
        boardContentSelectForm: BoardContentSelectForm,
        bookMarkSelectForm: BookMarkSelectForm,
        likeSelectForm: LikeSelectForm,
        likeCountSelectForm: LikeCountSelectForm
    ) {
        kotlin.runCatching {
            val wrapperBoardEntity = selectBoardContentUseCase.invoke(
                boardContentSelectForm,
                bookMarkSelectForm,
                likeSelectForm,
                likeCountSelectForm
            )
            _viewState.value = BoardContentViewState(wrapperBoardEntity, _viewState.value.commentEntity)

        }.onFailure {
            Log.d("BoardViewModel", "셀렉트 실패")
        }.getOrNull()
    }

    suspend fun insertBookMarkContent(
        insertBookMarkForm: InsertBookMarkForm
    ) {
        kotlin.runCatching {
            val wrapperBoardEntity = insertBookMarkBoardContentUseCase(
                insertBookMarkForm,
                _viewState.value.wrapperBoardEntity
            )
            _viewState.value = BoardContentViewState(wrapperBoardEntity, _viewState.value.commentEntity)
        }.onFailure {

        }.getOrNull()
    }

    suspend fun deleteBookMarkContent(
        deleteBookMarkForm: DeleteBookMarkForm
    ) {
        kotlin.runCatching {
            val wrapperBoardEntity = deleteBookMarkBoardContentUseCase(
                deleteBookMarkForm,
                _viewState.value.wrapperBoardEntity
            )
            _viewState.value = BoardContentViewState(wrapperBoardEntity, _viewState.value.commentEntity)
        }.onFailure {

        }.getOrNull()
    }

    suspend fun insertLikeContent(
        insertLikeForm: InsertLikeForm,
        likeCountSelectForm: LikeCountSelectForm
    ) {
        kotlin.runCatching {
            val wrapperBoardEntity = insertLikeBoardContentUseCase(
                insertLikeForm = insertLikeForm,
                likeCountSelectForm = likeCountSelectForm,
                _viewState.value.wrapperBoardEntity
            )
            _viewState.value = BoardContentViewState(wrapperBoardEntity, _viewState.value.commentEntity)
        }.onFailure {

        }.getOrNull()
    }

    suspend fun deleteLikeContent(
        deleteLikeForm: DeleteLikeForm,
        likeCountSelectForm: LikeCountSelectForm
    ) {
        kotlin.runCatching {
            val wrapperBoardEntity = deleteLikeBoardContentUseCase(
                deleteLikeForm = deleteLikeForm,
                likeCountSelectForm = likeCountSelectForm,
                _viewState.value.wrapperBoardEntity
            )
            _viewState.value = BoardContentViewState(wrapperBoardEntity, _viewState.value.commentEntity)
        }.onFailure {

        }.getOrNull()
    }

    suspend fun insertComment(
        commentInsertForm: CommentInsertForm
    ) {
        kotlin.runCatching {
            val commentEntity = writeCommentUseCase(commentInsertForm)
            _viewState.value =
                BoardContentViewState(_viewState.value.wrapperBoardEntity, commentEntity)
        }.onFailure {

        }.getOrNull()
    }


}