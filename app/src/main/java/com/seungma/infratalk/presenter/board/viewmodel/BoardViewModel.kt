package com.seungma.infratalk.presenter.board.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.seungma.infratalk.domain.board.entity.BoardListEntity
import com.seungma.infratalk.domain.board.entity.BoardWriteEntity
import com.seungma.infratalk.domain.board.usecase.AddBoardBookmarkUseCase
import com.seungma.infratalk.domain.board.usecase.AddBoardLikeUseCase
import com.seungma.infratalk.domain.board.usecase.DeleteBoardBookmarkUseCase
import com.seungma.infratalk.domain.board.usecase.DeleteBoardLikeUseCase
import com.seungma.infratalk.domain.board.usecase.DeleteBoardUseCase
import com.seungma.infratalk.domain.board.usecase.LoadBoardListUseCase
import com.seungma.infratalk.domain.board.usecase.UpdateBoardContentImagesUseCase
import com.seungma.infratalk.domain.board.usecase.WriteBoardContentUseCase
import com.seungma.infratalk.domain.chat.entity.ChatStartEntity
import com.seungma.infratalk.domain.chat.usecase.CheckChatRoomUseCase
import com.seungma.infratalk.domain.chat.usecase.CreateChatRoomUseCase
import com.seungma.infratalk.domain.user.GetUserInfoUseCase
import com.seungma.infratalk.domain.user.UserEntity
import com.seungma.infratalk.presenter.board.form.BoardBookmarkAddForm
import com.seungma.infratalk.presenter.board.form.BoardBookmarkDeleteForm
import com.seungma.infratalk.presenter.board.form.BoardBookmarksDeleteForm
import com.seungma.infratalk.presenter.board.form.BoardContentImagesUpdateForm
import com.seungma.infratalk.presenter.board.form.BoardContentInsertForm
import com.seungma.infratalk.presenter.board.form.BoardDeleteForm
import com.seungma.infratalk.presenter.board.form.BoardLikeAddForm
import com.seungma.infratalk.presenter.board.form.BoardLikeCountLoadForm
import com.seungma.infratalk.presenter.board.form.BoardLikeDeleteForm
import com.seungma.infratalk.presenter.board.form.BoardLikesDeleteForm
import com.seungma.infratalk.presenter.board.form.BoardListLoadForm
import com.seungma.infratalk.presenter.chat.form.ChatRoomCheckForm
import com.seungma.infratalk.presenter.chat.form.ChatRoomCreateForm
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.updateAndGet
import javax.inject.Inject

sealed class BoardViewEvent {
    data class Register(val boardWriteEntity: BoardWriteEntity) : BoardViewEvent()

    data class ChatStart(val chatStartEntity: ChatStartEntity) : BoardViewEvent()
    data class Error(val errorCode: Throwable) : BoardViewEvent()
}

class BoardViewModel @Inject constructor(
    private val writeBoardContentUseCase: WriteBoardContentUseCase,
    private val updateBoardContentImagesUseCase: UpdateBoardContentImagesUseCase,
    private val loadBoardListUseCase: LoadBoardListUseCase,
    private val addBoardBookmarkUseCase: AddBoardBookmarkUseCase,
    private val deleteBoardBookmarkUseCase: DeleteBoardBookmarkUseCase,
    private val addBoardLikeUseCase: AddBoardLikeUseCase,
    private val deleteBoardLikeUseCase: DeleteBoardLikeUseCase,
    private val createChatRoomUseCase: CreateChatRoomUseCase,
    private val getUserInfoUseCase: GetUserInfoUseCase,
    private val checkChatRoomUseCase: CheckChatRoomUseCase,
    private val deleteBoardUseCase: DeleteBoardUseCase
) : ViewModel() {
    private val _viewEvent = MutableSharedFlow<BoardViewEvent>()
    val viewEvent: SharedFlow<BoardViewEvent> = _viewEvent.asSharedFlow()

    private val _viewState =
        MutableStateFlow(BoardViewState(BoardListEntity(emptyList())))
    val viewState: StateFlow<BoardViewState> = _viewState.asStateFlow()

    data class BoardViewState(
        val boardListEntity: BoardListEntity
    )

    suspend fun writeBoardContent(
        boardContentInsertForm: BoardContentInsertForm
    ) {
        kotlin.runCatching {
            val boardInsertEntity =
                writeBoardContentUseCase(boardContentInsertForm = boardContentInsertForm)

            when (boardContentInsertForm.images.isNullOrEmpty()) {
                true -> {
                    _viewEvent.emit(
                        BoardViewEvent.Register(
                            boardWriteEntity = BoardWriteEntity(
                                isSuccess = true
                            )
                        )
                    )
                }

                false -> {
                    updateBoardContentImagesUseCase(
                        boardContentImagesUpdateForm = BoardContentImagesUpdateForm(
                            boardAuthorEmail = boardInsertEntity.boardAuthorEmail,
                            boardCreateTime = boardInsertEntity.boardCreteTime,
                            images = boardContentInsertForm.images
                        )
                    )
                    _viewEvent.emit(
                        BoardViewEvent.Register(
                            boardWriteEntity = BoardWriteEntity(
                                isSuccess = true
                            )
                        )
                    )
                }
            }

        }.onFailure {
            _viewEvent.emit(BoardViewEvent.Error(it))
        }
    }

    suspend fun loadBoardList(boardListLoadForm: BoardListLoadForm): BoardViewState {
        val result = kotlin.runCatching {
            val existingEntity = viewState.value.boardListEntity
            val newEntity = loadBoardListUseCase(boardListLoadForm = boardListLoadForm)
            when (boardListLoadForm.reload) {
                true -> newEntity.boardList
                false -> existingEntity.boardList + newEntity.boardList
            }
        }.onFailure {

        }.getOrNull()

        return result?.let {
            BoardViewState(boardListEntity = BoardListEntity(it)).apply {
                _viewState.value = this
            }
        } ?: viewState.value
    }

    suspend fun addLike(
        boardLikeAddForm: BoardLikeAddForm,
        boardLikeCountLoadForm: BoardLikeCountLoadForm
    ): BoardViewState {
        val result = kotlin.runCatching {
            addBoardLikeUseCase(
                boardLikeAddForm = boardLikeAddForm,
                boardLikeCountLoadForm = boardLikeCountLoadForm,
                boardListEntity = BoardListEntity(boardList = viewState.value.boardListEntity.boardList)
            )
        }.onFailure {

        }.getOrNull()

        return result?.let {
            BoardViewState(boardListEntity = it).apply {
                _viewState.value = this
            }
        } ?: viewState.value
    }

    suspend fun deleteLike(
        boardLikeDeleteForm: BoardLikeDeleteForm,
        boardLikeCountLoadForm: BoardLikeCountLoadForm,
    ): BoardViewState {
        val result = kotlin.runCatching {
            deleteBoardLikeUseCase(
                boardLikeDeleteForm = boardLikeDeleteForm,
                boardLikeCountLoadForm = boardLikeCountLoadForm,
                boardListEntity = BoardListEntity(boardList = viewState.value.boardListEntity.boardList)
            )
        }.onFailure {

        }.getOrNull()

        return result?.let {
            BoardViewState(boardListEntity = it).apply {
                _viewState.value = this
            }
        } ?: viewState.value
    }

    suspend fun addBookMark(
        boardBookmarkAddForm: BoardBookmarkAddForm
    ): BoardViewState {
        val result = kotlin.runCatching {
            addBoardBookmarkUseCase(
                boardBookmarkAddForm = boardBookmarkAddForm,
                boardListEntity = BoardListEntity(boardList = _viewState.value.boardListEntity.boardList)
            )
        }.onFailure {

        }.getOrNull()

        return result?.let {
            BoardViewState(boardListEntity = it).apply {
                _viewState.value = this
            }
        } ?: viewState.value
    }

    suspend fun deleteBookMark(
        boardBookmarkDeleteForm: BoardBookmarkDeleteForm
    ): BoardViewState {
        val result = kotlin.runCatching {
            deleteBoardBookmarkUseCase(
                boardBookmarkDeleteForm = boardBookmarkDeleteForm,
                boardListEntity = BoardListEntity(boardList = _viewState.value.boardListEntity.boardList)
            )
        }.onFailure {

        }.getOrNull()
        return result?.let {
            BoardViewState(boardListEntity = it).apply {
                _viewState.value = this
            }
        } ?: viewState.value
    }

    suspend fun startChat(
        chatRoomCreateForm: ChatRoomCreateForm,
        chatRoomCheckForm: ChatRoomCheckForm
    ) {
        kotlin.runCatching {

            val chatRoomCheckEntity = checkChatRoomUseCase(chatRoomCheckForm = chatRoomCheckForm)

            when (chatRoomCheckEntity.isChatRoom) {
                true -> {
                    _viewEvent.emit(
                        BoardViewEvent.ChatStart(
                            chatStartEntity = ChatStartEntity(
                                chatPartner = chatRoomCheckForm.member[1],
                                chatRoomId = chatRoomCheckEntity.chatRoomId,
                                isSuccess = true
                            )
                        )
                    )
                }

                false -> {
                    val chatRoomCreateEntity =
                        createChatRoomUseCase(chatRoomCreateForm = chatRoomCreateForm)

                    when (chatRoomCreateEntity.isSuccess) {
                        true -> _viewEvent.emit(
                            BoardViewEvent.ChatStart(
                                chatStartEntity = ChatStartEntity(
                                    chatPartner = chatRoomCheckForm.member[1],
                                    chatRoomId = chatRoomCreateEntity.chatRoomId,
                                    isSuccess = true
                                )
                            )
                        )

                        false -> _viewEvent.emit(
                            BoardViewEvent.ChatStart(
                                chatStartEntity = ChatStartEntity(
                                    chatPartner = chatRoomCheckForm.member[1],
                                    chatRoomId = null,
                                    isSuccess = false
                                )
                            )
                        )
                    }
                }
            }

        }.onFailure {
            _viewEvent.emit(BoardViewEvent.Error(it))
        }
    }

    fun getUserInfo(): UserEntity {
        return getUserInfoUseCase()
    }

    suspend fun deleteBoard(
        boardDeleteForm: BoardDeleteForm,
        boardBookmarksDeleteForm: BoardBookmarksDeleteForm,
        boardLikesDeleteForm: BoardLikesDeleteForm
    ): BoardViewState {
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