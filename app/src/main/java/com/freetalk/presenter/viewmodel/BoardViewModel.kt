package com.freetalk.presenter.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.freetalk.data.entity.BoardEntity
import com.freetalk.data.entity.BoardListEntity
import com.freetalk.data.remote.BoardInsetForm
import com.freetalk.data.remote.BoardUpdateForm
import com.freetalk.data.remote.ImagesRequest
import com.freetalk.usecase.SelectContentsUseCase
import com.freetalk.usecase.UpdateImageContentUseCase
import com.freetalk.usecase.WriteContentUseCase
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.coroutines.flow.*
import java.security.PrivateKey
import javax.inject.Inject

sealed class BoardViewEvent {
    data class Insert(val boardEntity: BoardEntity) : BoardViewEvent()
    data class Error(val errorCode: Throwable) : BoardViewEvent()
}

sealed class BoardViewState {
    data class Select(val boardListEntity: BoardListEntity?) : BoardViewState()
    data class Error(val errorCode: Throwable) : BoardViewState()
}


class BoardViewModel @Inject constructor(
    private val writeContentUseCase: WriteContentUseCase,
    private val updateImageContentUseCase: UpdateImageContentUseCase,
    private val selectContentsUseCase: SelectContentsUseCase
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

    suspend fun select(lastDocumentSnapshot: DocumentSnapshot?) {
        kotlin.runCatching {
            Log.d("BoardViewModel", "셀렉트 시작")
            val selectResult = selectContentsUseCase.selectContents(lastDocumentSnapshot)
            selectResult.boardList?.map {
                Log.d("BoardViewModel", it.content)
            }
            _viewState.value = BoardViewState.Select(selectResult)
        }.onFailure {
            Log.d("BoardViewModel", "셀렉트 실패")
        }

    }


}