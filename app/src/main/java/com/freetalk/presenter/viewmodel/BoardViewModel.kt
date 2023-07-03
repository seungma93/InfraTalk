package com.freetalk.presenter.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.freetalk.data.entity.*
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
    private val printBoardListUseCase: PrintBoardListUseCase,
    private val updateBookMarkBoardUseCase: UpdateBookMarkBoardUseCase,
    private val insertLikeBoardUseCase: InsertLikeBoardUseCase,
    private val deleteLikeBoardUseCase: DeleteLikeBoardUseCase,
    private val updateBookMarkBoardContentUseCase: UpdateBookMarkBoardContentUseCase
) : ViewModel() {
    private val _viewEvent = MutableSharedFlow<BoardViewEvent>()
    val viewEvent: SharedFlow<BoardViewEvent> = _viewEvent.asSharedFlow()

    // 처음 값 생성
    private val boardViewState = BoardViewState(PrintBoardListUseCase.WrapperBoardList(emptyList()), WrapperBoardEntity())
    private val _viewState = MutableStateFlow<BoardViewState>(boardViewState)
    val viewState: StateFlow<BoardViewState> = _viewState.asStateFlow()

    data class BoardViewState(
        val boardList: PrintBoardListUseCase.WrapperBoardList,
        val wrapperBoardEntity: WrapperBoardEntity
    )

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
            selectResult.wrapperBoardList.map {
                Log.d("BoardViewModel", it.boardEntity.content)
            }
            when (boardSelectForm.reload) {
                true -> selectResult.wrapperBoardList
                false -> _viewState.value.boardList.wrapperBoardList + selectResult.wrapperBoardList
            }
        }.onFailure {
            Log.d("BoardViewModel", "셀렉트 실패")
        }.getOrNull()

        return result?.let {
            BoardViewState(PrintBoardListUseCase.WrapperBoardList(it), _viewState.value.wrapperBoardEntity).apply {
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
                _viewState.value.boardList.wrapperBoardList
            )
            _viewState.value = BoardViewState(PrintBoardListUseCase.WrapperBoardList(newList), _viewState.value.wrapperBoardEntity)
        }.onFailure {
            Log.d("BoardViewModel", "북마크 업데이트 실패")
        }.getOrNull()
    }

    suspend fun insertLike(
        insertLikeForm: InsertLikeForm,
        likeCountSelectForm: LikeCountSelectForm
    ) {
        kotlin.runCatching {
            val boardList = insertLikeBoardUseCase(
                insertLikeForm,
                likeCountSelectForm,
                _viewState.value.boardList.wrapperBoardList
            )
            _viewState.value = BoardViewState(PrintBoardListUseCase.WrapperBoardList(boardList), _viewState.value.wrapperBoardEntity)
        }.onFailure {
            Log.d("BoardViewModel", "좋아요 업데이트 실패1" + it.message.toString())
        }.getOrNull()
    }

    suspend fun deleteLike(
        deleteLikeForm: DeleteLikeForm,
        likeCountSelectForm: LikeCountSelectForm
    ) {
        kotlin.runCatching {
            val boardList = deleteLikeBoardUseCase(
                deleteLikeForm,
                likeCountSelectForm,
                _viewState.value.boardList.wrapperBoardList
            )
            _viewState.value = BoardViewState(PrintBoardListUseCase.WrapperBoardList(boardList), _viewState.value.wrapperBoardEntity)
        }.onFailure {
            Log.d("BoardViewModel", "좋아요 업데이트 실패2")
        }.getOrNull()
    }



    suspend fun updateBookMarkContent(
        bookMarkUpdateForm: BookMarkUpdateForm,
        bookMarkSelectForm: BookMarkSelectForm,
        wrapperBoardEntity: WrapperBoardEntity
    ) {
        kotlin.runCatching {
            val wrapperBoardEntity = updateBookMarkBoardContentUseCase(
                bookMarkUpdateForm,
                bookMarkSelectForm,
                wrapperBoardEntity
            )
            _viewState.value = BoardViewState(_viewState.value.boardList, wrapperBoardEntity)
        }.onFailure {
            Log.d("BoardViewModel", "북마크 업데이트 실패")
        }.getOrNull()
    }

}