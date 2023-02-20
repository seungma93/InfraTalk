package com.freetalk.presenter.viewmodel

import androidx.lifecycle.ViewModel
import com.freetalk.data.entity.BoardEntity
import com.freetalk.data.remote.AuthData
import com.freetalk.data.remote.Respond
import com.freetalk.usecase.BoardUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

sealed class BoardViewEvent{
    data class Insert(val respond: Respond): BoardViewEvent()
}

class BoardViewModel(private val useCase: BoardUseCase): ViewModel() {
    private val _viewEvent = MutableSharedFlow<BoardViewEvent>()
    val viewEvent: SharedFlow<BoardViewEvent> = _viewEvent.asSharedFlow()

    suspend fun insert(boardEntity: BoardEntity) {
        _viewEvent.emit(BoardViewEvent.Insert(useCase.insert(boardEntity)))
    }
}