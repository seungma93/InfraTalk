package com.freetalk.presenter.viewmodel

import android.util.Log
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.freetalk.domain.entity.BoardListEntity
import com.freetalk.domain.entity.BoardWriteEntity
import com.freetalk.domain.entity.ChatMessageListEntity
import com.freetalk.domain.entity.ChatMessageSend
import com.freetalk.domain.entity.ChatPrimaryKeyEntity
import com.freetalk.domain.entity.ChatRoomEntity
import com.freetalk.domain.entity.ChatRoomLeave
import com.freetalk.domain.entity.ChatStartEntity
import com.freetalk.domain.entity.UserEntity
import com.freetalk.domain.usecase.AddBoardBookmarkUseCase
import com.freetalk.domain.usecase.AddBoardLikeUseCase
import com.freetalk.domain.usecase.CheckChatRoomUseCase
import com.freetalk.domain.usecase.CreateChatRoomUseCase
import com.freetalk.domain.usecase.DeleteBoardBookmarkUseCase
import com.freetalk.domain.usecase.DeleteBoardLikeUseCase
import com.freetalk.domain.usecase.DeleteBoardUseCase
import com.freetalk.domain.usecase.GetUserInfoUseCase
import com.freetalk.domain.usecase.LeaveChatRoomUseCase
import com.freetalk.domain.usecase.LoadBoardListUseCase
import com.freetalk.domain.usecase.LoadChatMessageListUseCase
import com.freetalk.domain.usecase.LoadChatRoomUseCase
import com.freetalk.domain.usecase.LoadMyBoardListUseCase
import com.freetalk.domain.usecase.LoadRealTimeChatMessageUseCase
import com.freetalk.domain.usecase.LoadRealTimeChatRoomListUseCase
import com.freetalk.domain.usecase.LoadRealTimeChatRoomUseCase
import com.freetalk.domain.usecase.SendChatMessageUseCase
import com.freetalk.domain.usecase.UpdateBoardContentImagesUseCase
import com.freetalk.domain.usecase.WriteBoardContentUseCase
import com.freetalk.presenter.form.BoardBookmarkAddForm
import com.freetalk.presenter.form.BoardBookmarkDeleteForm
import com.freetalk.presenter.form.BoardBookmarksDeleteForm
import com.freetalk.presenter.form.BoardContentImagesUpdateForm
import com.freetalk.presenter.form.BoardContentInsertForm
import com.freetalk.presenter.form.BoardDeleteForm
import com.freetalk.presenter.form.BoardLikeAddForm
import com.freetalk.presenter.form.BoardLikeCountLoadForm
import com.freetalk.presenter.form.BoardLikeDeleteForm
import com.freetalk.presenter.form.BoardLikesDeleteForm
import com.freetalk.presenter.form.BoardListLoadForm
import com.freetalk.presenter.form.ChatMessageListLoadForm
import com.freetalk.presenter.form.ChatMessageSendForm
import com.freetalk.presenter.form.ChatRoomCheckForm
import com.freetalk.presenter.form.ChatRoomCreateForm
import com.freetalk.presenter.form.ChatRoomLeaveForm
import com.freetalk.presenter.form.ChatRoomLoadForm
import com.freetalk.presenter.form.CommentDeleteForm
import com.freetalk.presenter.form.CommentRelatedBookmarksDeleteFrom
import com.freetalk.presenter.form.CommentRelatedLikesDeleteForm
import com.freetalk.presenter.form.MyBoardListLoadForm
import com.freetalk.presenter.form.RealTimeChatMessageLoadForm
import com.freetalk.presenter.form.RealTimeChatRoomLoadForm
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.updateAndGet
import kotlinx.coroutines.launch
import javax.inject.Inject

class MyBoardViewModel @Inject constructor(
    private val loadMyBoardListUseCase: LoadMyBoardListUseCase,
    private val addBoardBookmarkUseCase: AddBoardBookmarkUseCase,
    private val deleteBoardBookmarkUseCase: DeleteBoardBookmarkUseCase,
    private val addBoardLikeUseCase: AddBoardLikeUseCase,
    private val deleteBoardLikeUseCase: DeleteBoardLikeUseCase,
    private val getUserInfoUseCase: GetUserInfoUseCase,
    private val deleteBoardUseCase: DeleteBoardUseCase
) : ViewModel() {
    private val _viewState =
        MutableStateFlow(MyBoardViewState(BoardListEntity(emptyList())))
    private val viewState: StateFlow<MyBoardViewState> = _viewState.asStateFlow()

    data class MyBoardViewState(
        val boardListEntity: BoardListEntity
    )


    suspend fun loadMyBoardList(myBoardListLoadForm: MyBoardListLoadForm): MyBoardViewState {
        val result = kotlin.runCatching {
            val boardListEntity = loadMyBoardListUseCase(myBoardListLoadForm = myBoardListLoadForm)
            when (myBoardListLoadForm.reload) {
                true -> boardListEntity.boardList
                false -> _viewState.value.boardListEntity.boardList + boardListEntity.boardList
            }
        }.onFailure {

        }.getOrNull()

        return result?.let {
            MyBoardViewState(boardListEntity = BoardListEntity(it)).apply {
                _viewState.value = this
            }
        } ?: viewState.value
    }

    suspend fun addLike(
        boardLikeAddForm: BoardLikeAddForm,
        boardLikeCountLoadForm: BoardLikeCountLoadForm
    ): MyBoardViewState {
        val result = kotlin.runCatching {
            addBoardLikeUseCase(
                boardLikeAddForm = boardLikeAddForm,
                boardLikeCountLoadForm = boardLikeCountLoadForm,
                boardListEntity = BoardListEntity(boardList = viewState.value.boardListEntity.boardList)
            )
        }.onFailure {

        }.getOrNull()

        return result?.let {
            MyBoardViewState(boardListEntity = it).apply {
                _viewState.value = this
            }
        } ?: viewState.value
    }

    suspend fun deleteLike(
        boardLikeDeleteForm: BoardLikeDeleteForm,
        boardLikeCountLoadForm: BoardLikeCountLoadForm,
    ): MyBoardViewState {
        val result = kotlin.runCatching {
            deleteBoardLikeUseCase(
                boardLikeDeleteForm = boardLikeDeleteForm,
                boardLikeCountLoadForm = boardLikeCountLoadForm,
                boardListEntity = BoardListEntity(boardList = viewState.value.boardListEntity.boardList)
            )
        }.onFailure {

        }.getOrNull()

        return result?.let {
            MyBoardViewState(boardListEntity = it).apply {
                _viewState.value = this
            }
        } ?: viewState.value
    }

    suspend fun addBookMark(
        boardBookmarkAddForm: BoardBookmarkAddForm
    ): MyBoardViewState {
        val result = kotlin.runCatching {
            addBoardBookmarkUseCase(
                boardBookmarkAddForm = boardBookmarkAddForm,
                boardListEntity = BoardListEntity(boardList = _viewState.value.boardListEntity.boardList)
            )
        }.onFailure {

        }.getOrNull()

        return result?.let {
            MyBoardViewState(boardListEntity = it).apply {
                _viewState.value = this
            }
        } ?: viewState.value
    }

    suspend fun deleteBookMark(
        boardBookmarkDeleteForm: BoardBookmarkDeleteForm
    ): MyBoardViewState {
        val result = kotlin.runCatching {
            deleteBoardBookmarkUseCase(
                boardBookmarkDeleteForm = boardBookmarkDeleteForm,
                boardListEntity = BoardListEntity(boardList = _viewState.value.boardListEntity.boardList)
            )
        }.onFailure {

        }.getOrNull()
        return result?.let {
            MyBoardViewState(boardListEntity = it).apply {
                _viewState.value = this
            }
        } ?: viewState.value
    }

    fun getUserInfo(): UserEntity {
        return getUserInfoUseCase()
    }

    suspend fun deleteBoard(
        boardDeleteForm: BoardDeleteForm,
        boardBookmarksDeleteForm: BoardBookmarksDeleteForm,
        boardLikesDeleteForm: BoardLikesDeleteForm
    ): MyBoardViewState {
        val result = kotlin.runCatching {
            deleteBoardUseCase(
                boardDeleteForm = boardDeleteForm,
                boardBookmarksDeleteForm = boardBookmarksDeleteForm,
                boardLikesDeleteForm = boardLikesDeleteForm,
                boardListEntity = viewState.value.boardListEntity
            )

        }.onFailure {
            Log.d("BoardViewModel", "북마크 딜리트 실패")
        }.getOrNull()

        return result?.let {
            _viewState.updateAndGet { _ ->
                viewState.value.copy(boardListEntity = it)
            }
        } ?: viewState.value
    }

}