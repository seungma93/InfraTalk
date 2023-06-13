package com.freetalk.presenter.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.freetalk.data.entity.*
import com.freetalk.data.remote.*
import com.freetalk.usecase.*
import kotlinx.coroutines.flow.*
import javax.inject.Inject


class BoardContentViewModel @Inject constructor(
    private val updateBookMarkBoardContentUseCase: UpdateBookMarkBoardContentUseCase,
    private val selectBoardContentUseCase: SelectBoardContentUseCase,
    private val updateLikeBoardContentUseCase: UpdateLikeBoardContentUseCase
) : ViewModel() {

    // 처음 값 생성
    private val boardContentViewState = BoardContentViewState(WrapperBoardEntity())
    private val _viewState = MutableStateFlow<BoardContentViewState>(boardContentViewState)
    val viewState: StateFlow<BoardContentViewState> = _viewState.asStateFlow()

    data class BoardContentViewState(
        val wrapperBoardEntity: WrapperBoardEntity
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
            _viewState.value = BoardContentViewState(wrapperBoardEntity)

        }.onFailure {
            Log.d("BoardViewModel", "셀렉트 실패")
        }.getOrNull()
    }

    suspend fun updateBookMarkContent(
        bookMarkUpdateForm: BookMarkUpdateForm,
        bookMarkSelectForm: BookMarkSelectForm
    ) {
        kotlin.runCatching {
            val wrapperBoardEntity = updateBookMarkBoardContentUseCase(
                bookMarkUpdateForm,
                bookMarkSelectForm,
                _viewState.value.wrapperBoardEntity
            )
            _viewState.value = BoardContentViewState(wrapperBoardEntity)
        }.onFailure {
            Log.d("BoardViewModel", "북마크 업데이트 실패")
        }.getOrNull()
    }

    suspend fun updateLikeContent(
        likeUpdateForm: LikeUpdateForm,
        likeSelectForm: LikeSelectForm,
        likeCountSelectForm: LikeCountSelectForm
    ) {
        kotlin.runCatching {
            val wrapperBoardEntity = updateLikeBoardContentUseCase(
                likeUpdateForm,
                likeSelectForm,
                likeCountSelectForm,
                _viewState.value.wrapperBoardEntity
            )
            _viewState.value = BoardContentViewState(wrapperBoardEntity)
        }.onFailure {

        }.getOrNull()
    }


}