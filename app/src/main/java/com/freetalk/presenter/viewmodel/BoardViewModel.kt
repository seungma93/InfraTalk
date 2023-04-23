package com.freetalk.presenter.viewmodel

import androidx.lifecycle.ViewModel
import com.freetalk.data.entity.BoardEntity
import com.freetalk.data.remote.BoardInsetForm
import com.freetalk.data.remote.BoardUpdateForm
import com.freetalk.data.remote.ImagesRequest
import com.freetalk.usecase.UpdateImageContentUseCase
import com.freetalk.usecase.WriteContentUseCase
import kotlinx.coroutines.flow.*
import javax.inject.Inject

sealed class BoardViewEvent {
    data class Insert(val boardEntity: BoardEntity) : BoardViewEvent()
    data class Error(val errorCode: Throwable) : BoardViewEvent()
}

sealed class BoardViewState {
    //data class Select(val boardSelectData: BoardSelectData?) : BoardViewState()
    data class Error(val errorCode: Throwable) : BoardViewState()
}


class BoardViewModel @Inject constructor(
    private val writeContentUseCase: WriteContentUseCase,
    private val updateImageContentUseCase: UpdateImageContentUseCase
) : ViewModel() {
    private val _viewEvent = MutableSharedFlow<BoardViewEvent>()
    val viewEvent: SharedFlow<BoardViewEvent> = _viewEvent.asSharedFlow()
    private val _viewState = MutableStateFlow<BoardViewState?>(null)
    val viewState: StateFlow<BoardViewState?> = _viewState.asStateFlow()

    suspend fun insert(boardInsertForm: BoardInsetForm, imagesRequest: ImagesRequest?) {
        kotlin.runCatching {
            val writeContentUseCaseResult = writeContentUseCase.insert(boardInsertForm)
            val boardUpdateForm = BoardUpdateForm(
                writeContentUseCaseResult.author,
                writeContentUseCaseResult.title,
                writeContentUseCaseResult.content,
                imagesRequest?.imageUris,
                writeContentUseCaseResult.createTime,
                null
            )
            val result = updateImageContentUseCase.updateImageContent(boardUpdateForm)
            _viewEvent.emit(BoardViewEvent.Insert(result))
        }.onFailure {
            _viewEvent.emit(BoardViewEvent.Error(it))
        }
    }
/*
    suspend fun select() {
        Log.v("BoardViewModel", "셀렉트 시작")
        _viewState.value = BoardViewState.Select(useCase.select())
    }

 */
}