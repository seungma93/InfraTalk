package com.freetalk.presenter.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.freetalk.data.entity.BoardEntity
import com.freetalk.data.entity.BoardListEntity
import com.freetalk.data.entity.BookMarkableBoardEntity
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
    private val updateBookMarkUseCase: UpdateBookMarkUseCase
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

    suspend fun select(boardSelectForm: BoardSelectForm) {
        kotlin.runCatching {
            Log.d("BoardViewModel", "셀렉트 시작")
            val selectResult = printBoardListUseCase(boardSelectForm)
            selectResult.boardList.map {
                Log.d("BoardViewModel", it.boardEntity.content)
            }
            val boardListState = when (boardSelectForm.reload) {
                true -> selectResult.boardList
                false -> _viewState.value.boardListEntity.boardList + selectResult.boardList
            }

            _viewState.value = BoardViewState(BoardListEntity(boardListState))
        }.onFailure {
            Log.d("BoardViewModel", "셀렉트 실패")
        }

    }

    suspend fun touchBookMark(bookMarkUpdateForm: BookMarkUpdateForm) {
        kotlin.runCatching {
            val bookMarkableBoardEntity = updateBookMarkUseCase.invoke(bookMarkUpdateForm)
            val newList = mutableListOf<BookMarkableBoardEntity>()
            newList.addAll(_viewState.value.boardListEntity.boardList)
            newList.forEachIndexed { i, it ->
                if (it.boardEntity.author.email == bookMarkableBoardEntity.boardEntity.author.email && it.boardEntity.createTime == bookMarkableBoardEntity.boardEntity.createTime) {
                    newList.set(i, bookMarkableBoardEntity)
                }
            }
            _viewState.value = BoardViewState(BoardListEntity(newList))
        }
    }


}