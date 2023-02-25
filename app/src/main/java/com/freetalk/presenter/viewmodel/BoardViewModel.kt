package com.freetalk.presenter.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.freetalk.data.entity.BoardEntity
import com.freetalk.data.remote.AuthData
import com.freetalk.data.remote.BoardData
import com.freetalk.data.remote.Respond
import com.freetalk.presenter.activity.EndPoint
import com.freetalk.usecase.BoardUseCase
import kotlinx.coroutines.flow.*

sealed class BoardViewEvent{
    data class Insert(val respond: Respond): BoardViewEvent()
}

sealed class BoardViewState{
    data class Select(val boardData: BoardData?): BoardViewState()
}


class BoardViewModel(private val useCase: BoardUseCase): ViewModel() {
    private val _viewEvent = MutableSharedFlow<BoardViewEvent>()
    val viewEvent: SharedFlow<BoardViewEvent> = _viewEvent.asSharedFlow()
    private val _viewState = MutableStateFlow<BoardViewState?>(null)
    val viewState: StateFlow<BoardViewState?> = _viewState.asStateFlow()

    suspend fun insert(boardEntity: BoardEntity) {
        _viewEvent.emit(BoardViewEvent.Insert(useCase.insert(boardEntity)))
    }

    suspend fun select() {
        Log.v("BoardViewModel", "셀렉트 시작")
        _viewState.value = BoardViewState.Select(useCase.select())
    }
}