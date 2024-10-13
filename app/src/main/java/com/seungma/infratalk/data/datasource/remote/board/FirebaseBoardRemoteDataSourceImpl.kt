package com.seungma.infratalk.data.datasource.remote.board

import android.net.Uri
import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.seungma.infratalk.data.FailSelectException
import com.seungma.infratalk.data.datasource.remote.user.UserDataSource
import com.seungma.infratalk.data.model.request.board.BoardDeleteRequest
import com.seungma.infratalk.data.model.request.board.BoardInsertRequest
import com.seungma.infratalk.data.model.request.board.BoardMetaListSelectRequest
import com.seungma.infratalk.data.model.request.board.BoardSelectRequest
import com.seungma.infratalk.data.model.request.board.BoardUpdateRequest
import com.seungma.infratalk.data.model.request.board.MyBoardListLoadRequest
import com.seungma.infratalk.data.model.request.user.UserSelectRequest
import com.seungma.infratalk.data.model.response.board.BoardDeleteResponse
import com.seungma.infratalk.data.model.response.board.BoardInsertResponse
import com.seungma.infratalk.data.model.response.board.BoardMetaListResponse
import com.seungma.infratalk.data.model.response.board.BoardMetaResponse
import com.seungma.infratalk.domain.image.entity.ImagesResultEntity
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.tasks.await
import javax.inject.Inject



class FirebaseBoardRemoteDataSourceImpl @Inject constructor(
    private val database: FirebaseFirestore,
    private val userDataSource: UserDataSource
) : BoardDataSource {
    private var lastDocument: DocumentSnapshot? = null
    private var myBoardLastDocument: DocumentSnapshot? = null

    override suspend fun insertBoard(boardInsertRequest: BoardInsertRequest): BoardInsertResponse {
        return kotlin.runCatching {
            val createTime = boardInsertRequest.createTime

            database.collection("Board")
                .add(boardInsertRequest)
                .await()

            BoardInsertResponse(
                boardAuthorEmail = boardInsertRequest.authorEmail,
                boardCreteTime = createTime,
                isSuccess = true
            )
        }.onFailure {
            throw com.seungma.infratalk.data.FailInsertException("인서트에 실패 했습니다")
        }.getOrThrow()
    }

    private suspend fun getBoardDocuments(
        limit: Long,
        startAfter: DocumentSnapshot?
    ): QuerySnapshot {
        val query = database.collection("Board").orderBy("createTime", Query.Direction.DESCENDING)
            .limit(limit)
        return if (startAfter != null) {
            query.startAfter(startAfter).get().await()
        } else {
            query.get().await()
        }
    }

    override suspend fun selectBoardMetaList(boardMetaListSelectRequest: BoardMetaListSelectRequest): BoardMetaListResponse =
        coroutineScope {
            kotlin.runCatching {
                val snapshot = when (boardMetaListSelectRequest.reload) {
                    true -> getBoardDocuments(10, null)
                    false -> getBoardDocuments(10, lastDocument)
                }
                lastDocument = snapshot.documents.lastOrNull()

                lastDocument?.let {
                    Log.d("BoardDataSource", "마지막 메시지 내용 :" + it.data?.get("content") )
                }


                snapshot.documents.map {
                    val authorEmail = it.data?.get("authorEmail")?.let { it as String } ?: error("")
                    val asyncUserInfo = async {
                        userDataSource
                            .selectUserInfo(UserSelectRequest(userEmail = authorEmail))
                    }
                    it to asyncUserInfo
                }.map { (it, deferred) ->
                    BoardMetaResponse(
                        author = deferred.await(),
                        title = it.data?.get("title") as? String,
                        content = it.data?.get("content") as? String,
                        images = (it.data?.get("images") as? List<String>)?.let {
                            ImagesResultEntity(it.map { Uri.parse(it) }, emptyList())
                        },
                        createTime = (it.data?.get("createTime") as? Timestamp)?.toDate(),
                        editTime = (it.data?.get("editTime") as? Timestamp)?.toDate()
                    )
                }.let {
                    BoardMetaListResponse(it)
                }
            }.onFailure {
                throw FailSelectException("셀렉트에 실패 했습니다", it)
            }.getOrThrow()
        }


    override suspend fun updateBoard(boardUpdateRequest: BoardUpdateRequest): BoardMetaResponse {
        return kotlin.runCatching {
            database.collection("Board")
                .whereEqualTo("author.email", boardUpdateRequest.author.email)
                .whereEqualTo("createTime", boardUpdateRequest.createTime).get().await().let {

                    boardUpdateRequest.images?.let { images ->

                        boardUpdateRequest.editTime.let { date ->

                            when (boardUpdateRequest.content) {
                                null -> {
                                    val updates = mapOf(
                                        "images" to boardUpdateRequest.images,
                                        "editTime" to boardUpdateRequest.editTime
                                    )
                                    it.documents[0].reference.update(updates).await()
                                }

                                else -> {
                                    val updates = mapOf(
                                        "content" to boardUpdateRequest.content,
                                        "images" to boardUpdateRequest.images,
                                        "editTime" to boardUpdateRequest.editTime
                                    )
                                    it.documents[0].reference.update(updates).await()
                                }
                            }

                        } ?: run {
                            val updates = mapOf(
                                "images" to boardUpdateRequest.images
                            )
                            it.documents[0].reference.update(updates).await()

                        }

                    } ?: run {
                        val updates = mapOf(
                            "content" to boardUpdateRequest.content,
                            "editTime" to boardUpdateRequest.editTime
                        )
                        it.documents[0].reference.update(updates).await()

                    }
                }
            BoardMetaResponse(
                userDataSource.getUserMe(),
                boardUpdateRequest.title,
                boardUpdateRequest.content,
                null,
                boardUpdateRequest.createTime,
                boardUpdateRequest.editTime
            )

        }.onFailure {
            throw com.seungma.infratalk.data.FailUpdatetException("업데이트 실패")
        }.getOrThrow()
    }

    override suspend fun selectBoard(boardSelectRequest: BoardSelectRequest): BoardMetaResponse =
        with(boardSelectRequest) {
            return kotlin.runCatching {
                val snapshot = database.collection("Board")
                    .whereEqualTo("authorEmail", boardAuthorEmail)
                    .whereEqualTo("createTime", boardCreateTime).get().await()

                snapshot.documents.firstOrNull()?.let {
                    val authorEmail = it.data?.get("authorEmail")?.let { it as String } ?: error("")
                    BoardMetaResponse(
                        author = userDataSource
                            .selectUserInfo(UserSelectRequest(userEmail = authorEmail)),
                        title = it.data?.get("title") as? String,
                        content = it.data?.get("content") as? String,
                        images = (it.data?.get("images") as? List<String>)?.let {
                            ImagesResultEntity(it.map { Uri.parse(it) }, emptyList())
                        },
                        createTime = (it.data?.get("createTime") as? Timestamp)?.toDate(),
                        editTime = (it.data?.get("editTime") as? Timestamp)?.toDate()
                    )
                } ?: run {
                    throw com.seungma.infratalk.data.FailSelectBoardContentException("보드 콘텐츠 셀렉트 실패")
                }
            }.onFailure {
                throw com.seungma.infratalk.data.FailSelectBoardContentException("보드 콘텐츠 셀렉트 실패")
            }.getOrThrow()

        }

    private suspend fun getMyBoardDocuments(
        limit: Long,
        startAfter: DocumentSnapshot?
    ): QuerySnapshot {
        val query = database.collection("Board")
            .whereEqualTo("authorEmail", userDataSource.getUserMe().email)
            .orderBy("createTime", Query.Direction.DESCENDING)
            .limit(limit)
        return if (startAfter != null) {
            query.startAfter(startAfter).get().await()
        } else {
            query.get().await()
        }
    }

    override suspend fun loadMyBoardList(myBoardListLoadRequest: MyBoardListLoadRequest): BoardMetaListResponse {
            return runCatching {
                val snapshot = when (myBoardListLoadRequest.reload) {
                    true -> getMyBoardDocuments(10, null)
                    false -> getMyBoardDocuments(10, myBoardLastDocument)
                }
                myBoardLastDocument = snapshot.documents.lastOrNull()

                snapshot.documents.map {
                    BoardMetaResponse(
                        author = userDataSource.getUserMe(),
                        title = it.data?.get("title") as? String,
                        content = it.data?.get("content") as? String,
                        images = (it.data?.get("images") as? List<String>)?.let {
                            ImagesResultEntity(it.map { Uri.parse(it) }, emptyList())
                        },
                        createTime = (it.data?.get("createTime") as? Timestamp)?.toDate(),
                        editTime = (it.data?.get("editTime") as? Timestamp)?.toDate()
                    )
                }.let {
                    BoardMetaListResponse(it)
                }
            }.onFailure {
                throw FailSelectException("셀렉트에 실패 했습니다", it)
            }.getOrThrow()
        }

    override suspend fun deleteBoard(boardDeleteRequest: BoardDeleteRequest): BoardDeleteResponse {
        return kotlin.runCatching {
            database.collection("Board")
                .whereEqualTo("authorEmail", boardDeleteRequest.boardAuthorEmail)
                .whereEqualTo("createTime", boardDeleteRequest.boardCreateTime)
                .get().await().apply {
                    documents.forEach { it.reference.delete().await() }
                }

            BoardDeleteResponse(
                boardAuthorEmail = boardDeleteRequest.boardAuthorEmail,
                boardCreateTime = boardDeleteRequest.boardCreateTime,
                isSuccess = true
            )
        }.onFailure {
            throw com.seungma.infratalk.data.FailDeleteCommentException("댓글 셀렉트에 실패 했습니다")
        }.getOrThrow()
    }

    override suspend fun loadMyBookmarkBoardList(): BoardMetaListResponse = coroutineScope {
        kotlin.runCatching {
            val userResponse = userDataSource.getUserMe()
            val snapshot = database.collection("BoardBookmark")
                .whereEqualTo("userEmail", userResponse.email)
                .get().await()
            snapshot.documents.map {
                val boardAuthorEmail = it.data?.get("boardAuthorEmail") as? String
                val boardCreateTime = it.data?.get("boardCreateTime") as? Timestamp
                Log.d("seungma", "보드이메일" + boardAuthorEmail)
                Log.d("seungma", "보드시간" + boardCreateTime)
                boardAuthorEmail to boardCreateTime
            }.map { (boardAuthorEmail, boardCreateTime) ->

                val asyncBoard = async {
                    database.collection("Board")
                        .whereEqualTo("authorEmail", boardAuthorEmail)
                        .whereEqualTo("createTime", boardCreateTime)
                        .get().await()
                }
                asyncBoard
            }.map {
                val boardSnapshot = it.await()
                val asyncUserInfo = boardSnapshot.documents.firstOrNull()?.let {
                    val authorEmail = it.data?.get("authorEmail")?.let { it as String } ?: error("")
                    async {
                        userDataSource
                            .selectUserInfo(UserSelectRequest(userEmail = authorEmail))
                    }
                } ?: run {
                    throw com.seungma.infratalk.data.FailSelectBoardContentException("보드 콘텐츠 셀렉트 실패")
                }
                boardSnapshot to asyncUserInfo
            }.map { (it, deferred) ->
                it.documents.firstNotNullOf {
                    BoardMetaResponse(
                        author = deferred.await(),
                        title = it.data?.get("title") as? String,
                        content = it.data?.get("content") as? String,
                        images = (it.data?.get("images") as? List<String>)?.let {
                            ImagesResultEntity(it.map { Uri.parse(it) }, emptyList())
                        },
                        createTime = (it.data?.get("createTime") as? Timestamp)?.toDate(),
                        editTime = (it.data?.get("editTime") as? Timestamp)?.toDate()
                    )
                }
            }.let {
                BoardMetaListResponse(it)
            }

        }.onFailure {
            throw com.seungma.infratalk.data.FailDeleteBookMarkException("북마크 딜리트에 실패했습니다")
        }.getOrThrow()
    }

    override suspend fun loadMyLikeBoardList(): BoardMetaListResponse = coroutineScope {
        kotlin.runCatching {
            val userResponse = userDataSource.getUserMe()
            val snapshot = database.collection("BoardLike")
                .whereEqualTo("userEmail", userResponse.email)
                .get().await()
            snapshot.documents.map {
                val boardAuthorEmail = it.data?.get("boardAuthorEmail") as? String
                val boardCreateTime = it.data?.get("boardCreateTime") as? Timestamp
                boardAuthorEmail to boardCreateTime
            }.map { (boardAuthorEmail, boardCreateTime) ->
                val asyncBoard = async {
                    database.collection("Board")
                        .whereEqualTo("authorEmail", boardAuthorEmail)
                        .whereEqualTo("createTime", boardCreateTime)
                        .get().await()
                }
                asyncBoard
            }.map {
                val boardSnapshot = it.await()
                val asyncUserInfo = boardSnapshot.documents.firstOrNull()?.let {
                    val authorEmail = it.data?.get("authorEmail")?.let { it as String } ?: error("")
                    async {
                        userDataSource
                            .selectUserInfo(UserSelectRequest(userEmail = authorEmail))
                    }
                } ?: run {
                    throw com.seungma.infratalk.data.FailSelectBoardContentException("보드 콘텐츠 셀렉트 실패")
                }
                boardSnapshot to asyncUserInfo
            }.map { (it, deferred) ->
                it.documents.firstNotNullOf {
                    BoardMetaResponse(
                        author = deferred.await(),
                        title = it.data?.get("title") as? String,
                        content = it.data?.get("content") as? String,
                        images = (it.data?.get("images") as? List<String>)?.let {
                            ImagesResultEntity(it.map { Uri.parse(it) }, emptyList())
                        },
                        createTime = (it.data?.get("createTime") as? Timestamp)?.toDate(),
                        editTime = (it.data?.get("editTime") as? Timestamp)?.toDate()
                    )
                }
            }.let {
                BoardMetaListResponse(it)
            }

        }.onFailure {
            throw com.seungma.infratalk.data.FailDeleteBookMarkException("북마크 딜리트에 실패했습니다")
        }.getOrThrow()
    }

}
