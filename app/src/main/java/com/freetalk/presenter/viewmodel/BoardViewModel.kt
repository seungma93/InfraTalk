package com.freetalk.presenter.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.freetalk.data.entity.BoardEntity
import com.freetalk.data.entity.BoardListEntity
import com.freetalk.data.remote.BoardInsetForm
import com.freetalk.data.remote.BoardSelectForm
import com.freetalk.data.remote.BoardUpdateForm
import com.freetalk.data.remote.ImagesRequest
import com.freetalk.usecase.PrintBoardListUesCase
import com.freetalk.usecase.UpdateImageContentUseCase
import com.freetalk.usecase.WriteContentUseCase
import kotlinx.coroutines.flow.*
import javax.inject.Inject

sealed class BoardViewEvent {
    data class Insert(val boardEntity: BoardEntity) : BoardViewEvent()
    data class Error(val errorCode: Throwable) : BoardViewEvent()
}

class BoardViewModel @Inject constructor(
    private val writeContentUseCase: WriteContentUseCase,
    private val updateImageContentUseCase: UpdateImageContentUseCase,
    private val printBoardListUseCase: PrintBoardListUesCase
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
                Log.d("BoardViewModel", it.content)
            }
            _viewState.value = BoardViewState(selectResult)
        }.onFailure {
            Log.d("BoardViewModel", "셀렉트 실패")
        }

    }


}