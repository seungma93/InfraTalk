package com.freetalk.presenter.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.freetalk.domain.entity.BoardListEntity
import com.freetalk.domain.entity.BoardWriteEntity
import com.freetalk.domain.entity.ChatRoomCheckEntity
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
import com.freetalk.domain.usecase.LoadBoardListUseCase
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
import com.freetalk.presenter.form.ChatRoomCheckForm
import com.freetalk.presenter.form.ChatRoomCreateForm
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
            val boardListEntity = loadBoardListUseCase(boardListLoadForm = boardListLoadForm)
            when (boardListLoadForm.reload) {
                true -> boardListEntity.boardList
                false -> _viewState.value.boardListEntity.boardList + boardListEntity.boardList
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

            when(chatRoomCheckEntity.isChatRoom) {
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
                    val chatRoomCreateEntity = createChatRoomUseCase(chatRoomCreateForm = chatRoomCreateForm)

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