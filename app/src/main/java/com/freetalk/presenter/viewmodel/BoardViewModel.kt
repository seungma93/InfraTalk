package com.freetalk.presenter.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.freetalk.data.entity.BoardEntity
import com.freetalk.data.entity.BoardListEntity
import com.freetalk.data.remote.*
import com.freetalk.usecase.*
import kotlinx.coroutines.flow.*
import javax.inject.Inject

sealed class BoardViewEvent {
    data class Insert(val boardEntity: BoardEntity) : BoardViewEvent()
    data class Error(val errorCode: Throwable) : BoardViewEvent()
}

class BoardViewModel @Inject constructor(
    private val writeContentUseCase: WriteContentUseCase,
    private val updateImageContentUseCase: UpdateImageContentUseCase,
    private val printBoardListUseCase: PrintBoardListUesCase,
    private val updateBookMarkBoardUseCase: UpdateBookMarkBoardUseCase,
    private val updateLikeBoardUseCase: UpdateLikeBoardUseCase
) : ViewModel() {
    private val _viewEvent = MutableSharedFlow<BoardViewEvent>()
    val viewEvent: SharedFlow<BoardViewEvent> = _viewEvent.asSharedFlow()
    private val boardViewState = BoardViewState(BoardListEntity(emptyList()))
    private val _viewState = MutableStateFlow<BoardViewState>(boardViewState)
    val viewState: StateFlow<BoardViewState> = _viewState.asStateFlow()

    data class BoardViewState(val boardListEntity: BoardListEntity)


    suspend fun insert(boardInsertForm: BoardInsetForm, imagesRequest: ImagesRequest) {
        kotlin.runCatching {
            val writeContentUseCaseResult = writeContentUseCase.insert(boardInsertForm)
            val boardUpdateForm = BoardUpdateForm(
                writeContentUseCaseResult.author,
                writeContentUseCaseResult.title,
                writeContentUseCaseResult.content,
                imagesRequest.imageUris,
                writeContentUseCaseResult.createTime,
                null
            )
            val result = updateImageContentUseCase.updateImageContent(boardUpdateForm)
            _viewEvent.emit(BoardViewEvent.Insert(result))
        }.onFailure {
            _viewEvent.emit(BoardViewEvent.Error(it))
        }
    }

    suspend fun select(boardSelectForm: BoardSelectForm): BoardViewState {
        val result = kotlin.runCatching {
            Log.d("BoardViewModel", "셀렉트 시작")
            val selectResult = printBoardListUseCase(boardSelectForm)
            selectResult.boardList.map {
                Log.d("BoardViewModel", it.boardEntity.content)
            }
            when (boardSelectForm.reload) {
                true -> selectResult.boardList
                false -> _viewState.value.boardListEntity.boardList + selectResult.boardList
            }
        }.onFailure {
            Log.d("BoardViewModel", "셀렉트 실패")
        }.getOrNull()

        return result?.let {
            BoardViewState(BoardListEntity(it)).apply {
                _viewState.value = this
            }
        } ?: viewState.value
    }

    suspend fun updateBookMark(
        bookMarkUpdateForm: BookMarkUpdateForm,
        bookMarkSelectForm: BookMarkSelectForm,
    ) {
        kotlin.runCatching {
            val newList = updateBookMarkBoardUseCase(
                bookMarkUpdateForm,
                bookMarkSelectForm,
                _viewState.value.boardListEntity.boardList
            )
            _viewState.value = BoardViewState(BoardListEntity(newList))
        }.onFailure {
            Log.d("BoardViewModel", "북마크 업데이트 실패")
        }.getOrNull()
    }

    suspend fun updateLike(
        likeUpdateForm: LikeUpdateForm,
        likeSelectForm: LikeSelectForm,
        likeSelectCountSelectForm: LikeCountSelectForm
    ) {
        kotlin.runCatching {
            val newList = updateLikeBoardUseCase(
                likeUpdateForm,
                likeSelectForm,
                likeSelectCountSelectForm,
                _viewState.value.boardListEntity.boardList
            )
            _viewState.value = BoardViewState(BoardListEntity(newList))
        }.onFailure {
            Log.d("BoardViewModel", "좋아요 업데이트 실패")
        }.getOrNull()
    }


}