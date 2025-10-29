package com.sm.infratalk.presenter.board.components

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.rememberAsyncImagePainter
import com.google.android.gms.common.util.CollectionUtils.listOf
import com.sm.infratalk.R
import com.sm.infratalk.domain.board.entity.BoardContentPrimaryKeyEntity
import com.sm.infratalk.domain.board.entity.BoardEntity
import com.sm.infratalk.domain.board.entity.BoardMetaEntity
import com.sm.infratalk.domain.board.entity.BookmarkEntity
import com.sm.infratalk.domain.board.entity.LikeCountEntity
import com.sm.infratalk.domain.board.entity.LikeEntity
import com.sm.infratalk.domain.user.entity.UserEntity
import com.sm.infratalk.presenter.board.form.BoardBookmarkAddForm
import com.sm.infratalk.presenter.board.form.BoardBookmarkDeleteForm
import com.sm.infratalk.presenter.board.form.BoardBookmarksDeleteForm
import com.sm.infratalk.presenter.board.form.BoardDeleteForm
import com.sm.infratalk.presenter.board.form.BoardLikeAddForm
import com.sm.infratalk.presenter.board.form.BoardLikeCountLoadForm
import com.sm.infratalk.presenter.board.form.BoardLikeDeleteForm
import com.sm.infratalk.presenter.board.form.BoardLikesDeleteForm
import com.sm.infratalk.presenter.board.form.BoardListLoadForm
import com.sm.infratalk.presenter.board.viewmodel.BoardViewModel
import com.sm.infratalk.presenter.chat.form.ChatRoomCheckForm
import com.sm.infratalk.presenter.chat.form.ChatRoomCreateForm
import com.sm.infratalk.presenter.main.activity.EndPoint
import com.sm.infratalk.presenter.main.activity.Navigable
import kotlinx.coroutines.launch
import java.util.Date


@Composable
fun BoardScreen(
    viewModel: BoardViewModel
) {
    var isLoading by remember { mutableStateOf(false) }
    var isLoadingMore by remember { mutableStateOf(false) }
    var isRefreshing by remember { mutableStateOf(false) }
    val context = LocalContext.current

    val viewState by viewModel.viewState.collectAsStateWithLifecycle()

    // ViewState 변경 감지 로그
    LaunchedEffect(viewState) {
        Log.d("BoardScreen", "ViewState 변경됨! 아이템 개수: ${viewState.boardListEntity.boardList.size}")
        Log.d(
            "BoardScreen",
            "첫 번째 아이템 북마크 상태: ${viewState.boardListEntity.boardList.firstOrNull()?.bookmarkEntity?.isBookmark}"
        )
        Log.d(
            "BoardScreen",
            "첫 번째 아이템 좋아요 상태: ${viewState.boardListEntity.boardList.firstOrNull()?.likeEntity?.isLike}"
        )
    }

    // 초기 데이터 로
    LaunchedEffect(Unit) {
        isLoading = true
        try {
            viewModel.loadBoardList(BoardListLoadForm(reload = true))
        } catch (e: Exception) {
            Log.d("BoardScreen", "게시글 로드 실패", e)
        } finally {
            isLoading = false
        }
    }

    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }

    } else {
        val coroutineScope = rememberCoroutineScope()

        // Pull-to-refresh 로직
        val onRefresh:() -> Unit = {
            isRefreshing = true
            coroutineScope.launch {
                try {
                    Log.d("BoardScreen", "Pull-to-refresh 시작")
                    viewModel.loadBoardList(BoardListLoadForm(reload = true))
                    Log.d("BoardScreen", "Pull-to-refresh 완료")
                } catch (e: Exception) {
                    Log.e("BoardScreen", "Pull-to-refresh 실패", e)
                } finally {
                    isRefreshing = false
                }
            }
        }

        SwipeRefresh(
            state = rememberSwipeRefreshState(isRefreshing),
            onRefresh = onRefresh,
            modifier = Modifier.fillMaxSize()
        ) {
            BoardItemList(
            items = viewState.boardListEntity.boardList,
            onItemClick = {
                val endPoint = EndPoint.BoardContent(
                    boardContentPrimaryKeyEntity = BoardContentPrimaryKeyEntity(
                        boardAuthorEmail = it.boardMetaEntity.author.email,
                        boardCreateTime = it.createTime
                    )
                )
                (context as? Navigable)?.navigateFragment(endPoint)
            },
            onBookmarkClick = {
                Log.d(
                    "BoardScreen",
                    "북마크 클릭! 현재 상태: ${it.bookmarkEntity.isBookmark}, 게시글: ${it.boardMetaEntity.title}"
                )

                when (it.bookmarkEntity.isBookmark) {
                    true -> {
                        coroutineScope.launch {
                            Log.d("BoardScreen", "북마크 삭제 시작")
                            val result = viewModel.deleteBookMark(
                                BoardBookmarkDeleteForm(
                                    boardAuthorEmail = it.boardMetaEntity.author.email,
                                    boardCreateTime = it.boardMetaEntity.createTime
                                )
                            )
                            Log.d(
                                "BoardScreen",
                                "북마크 삭제 완료, 결과 아이템 개수: ${result.boardListEntity.boardList.size}"
                            )
                        }
                    }

                    false -> {
                        coroutineScope.launch {
                            Log.d("BoardScreen", "북마크 추가 시작")
                            val result = viewModel.addBookMark(
                                BoardBookmarkAddForm(
                                    boardAuthorEmail = it.boardMetaEntity.author.email,
                                    boardCreateTime = it.boardMetaEntity.createTime
                                )
                            )
                            Log.d(
                                "BoardScreen",
                                "북마크 추가 완료, 결과 아이템 개수: ${result.boardListEntity.boardList.size}"
                            )
                        }
                    }
                }


            },
            onLikeClick = {
                Log.d(
                    "BoardScreen",
                    "좋아요 클릭! 현재 상태: ${it.likeEntity.isLike}, 게시글: ${it.boardMetaEntity.title}"
                )

                when (it.likeEntity.isLike) {
                    true -> {
                        coroutineScope.launch {
                            Log.d("BoardScreen", "좋아요 삭제 시작")
                            val result = viewModel.deleteLike(
                                boardLikeDeleteForm = BoardLikeDeleteForm(
                                    boardAuthorEmail = it.boardMetaEntity.author.email,
                                    boardCreateTime = it.boardMetaEntity.createTime
                                ),
                                boardLikeCountLoadForm = BoardLikeCountLoadForm(
                                    boardAuthorEmail = it.boardMetaEntity.author.email,
                                    boardCreateTime = it.boardMetaEntity.createTime
                                )
                            )
                            Log.d(
                                "BoardScreen",
                                "좋아요 삭제 완료, 결과 아이템 개수: ${result.boardListEntity.boardList.size}"
                            )
                        }
                    }

                    false -> {
                        coroutineScope.launch {
                            Log.d("BoardScreen", "좋아요 추가 시작")
                            val result = viewModel.addLike(
                                boardLikeAddForm = BoardLikeAddForm(
                                    boardAuthorEmail = it.boardMetaEntity.author.email,
                                    boardCreateTime = it.boardMetaEntity.createTime
                                ),
                                boardLikeCountLoadForm = BoardLikeCountLoadForm(
                                    boardAuthorEmail = it.boardMetaEntity.author.email,
                                    boardCreateTime = it.boardMetaEntity.createTime
                                )
                            )
                            Log.d(
                                "BoardScreen",
                                "좋아요 추가 완료, 결과 아이템 개수: ${result.boardListEntity.boardList.size}"
                            )
                        }
                    }
                }
            }, onChatClick = {
                coroutineScope.launch {
                    val userEntity = viewModel.getUserMe()
                    val member = listOf(userEntity.email, it.boardMetaEntity.author.email)
                    viewModel.startChat(
                        chatRoomCreateForm = ChatRoomCreateForm(member = member),
                        chatRoomCheckForm = ChatRoomCheckForm(member = member)
                    )
                }

            }, onRemove = {
                coroutineScope.launch {
                    viewModel.deleteBoard(
                        boardDeleteForm = BoardDeleteForm(
                            boardAuthorEmail = it.boardMetaEntity.author.email,
                            boardCreateTime = it.boardMetaEntity.createTime
                        ),
                        boardBookmarksDeleteForm = BoardBookmarksDeleteForm(
                            boardAuthorEmail = it.boardMetaEntity.author.email,
                            boardCreateTime = it.boardMetaEntity.createTime
                        ),
                        boardLikesDeleteForm = BoardLikesDeleteForm(
                            boardAuthorEmail = it.boardMetaEntity.author.email,
                            boardCreateTime = it.boardMetaEntity.createTime
                        ),
                    )
                }

            }, onLoadMore = {
                // 중복 호출 방지
                if (!isLoadingMore) {
                    isLoadingMore = true
                    coroutineScope.launch {
                        try {
                            Log.d("BoardScreen", "더보기 로드 시작")
                            viewModel.loadBoardList(BoardListLoadForm(reload = false))
                            Log.d("BoardScreen", "더보기 로드 완료")
                        } catch (e: Exception) {
                            Log.e("BoardScreen", "더보기 로드 실패", e)
                        } finally {
                            isLoadingMore = false
                        }
                    }
                }
            }, isLoadingMore = isLoadingMore
        )
        }
    }
}

// 리스트 전체
@Composable
fun BoardItemList(
    items: List<BoardEntity>,
    onItemClick: (BoardEntity) -> Unit,
    onBookmarkClick: (BoardEntity) -> Unit,
    onLikeClick: (BoardEntity) -> Unit,
    onChatClick: (BoardEntity) -> Unit,
    onRemove: (BoardEntity) -> Unit,
    onLoadMore: () -> Unit,
    isLoadingMore: Boolean = false
) {
    // 스크롤 상태 관리
    val listState = rememberLazyListState()

    // 스크롤 감지 및 더보기 호출
    LaunchedEffect(listState, isLoadingMore) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo }
            .collect { visibleItems ->
                if (visibleItems.isNotEmpty() && !isLoadingMore) {
                    val lastVisibleItem = visibleItems.last()
                    val totalItems = listState.layoutInfo.totalItemsCount

                    // 마지막에서 3번째 아이템이 보이면 더보기 호출
                    if (lastVisibleItem.index >= totalItems - 3) {
                        Log.d(
                            "BoardScreen",
                            "스크롤 감지: 더보기 호출 (${lastVisibleItem.index}/${totalItems})"
                        )
                        onLoadMore()
                    }
                }
            }
    }

    LazyColumn(
        state = listState  // 스크롤 상태 연결
    ) {
        items(items) { item ->
            BoardItemRow(
                item = item,
                onClick = onItemClick,
                onBookmarkClick = onBookmarkClick,
                onLikeClick = onLikeClick,
                onChatClick = onChatClick
            )
        }

        // 로딩 인디케이터는 로딩 중일 때만 표시
        if (isLoadingMore) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                    Log.d("BoardScreen", "로딩 인디케이터 표시 중...")
                }
            }
        }

    }
}

// 아이템
@Composable
fun BoardItemRow(
    item: BoardEntity,
    onClick: (BoardEntity) -> Unit,
    onBookmarkClick: (BoardEntity) -> Unit,
    onLikeClick: (BoardEntity) -> Unit,
    onChatClick: (BoardEntity) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick(item) }
            .padding(16.dp)
    ) {
        // TODO("작성자, 날짜")
        Row(modifier = Modifier.fillMaxWidth()) {
            Image(
                painter = item.boardMetaEntity.images?.let {
                    rememberAsyncImagePainter(it.successUris)
                } ?: run {
                    painterResource(id = R.drawable.ic_avatar)
                },
                contentDescription = stringResource(R.string.app_name),
                modifier = Modifier, // 크기, 패딩 등 지정 가능
                contentScale = ContentScale.Crop // 이미지 크기 조절 방식
            )
            Column {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp),
                    text = item.boardMetaEntity.author.nickname
                )
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp),
                    text = item.boardMetaEntity.createTime.toString()
                )
            }
        }

        // TODO("제목, 내용")
        Text(modifier = Modifier.fillMaxWidth(), text = item.boardMetaEntity.title)
        Text(modifier = Modifier.fillMaxWidth(), text = item.boardMetaEntity.content)
        // TODO("버튼")



        item.boardMetaEntity.images?.successUris?.takeIf { it.isNotEmpty() }?.let { items ->
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    ,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(horizontal = 16.dp),
            ) {
                items(items) { item ->
                    ImageItemRow(item = item)
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp, horizontal = 10.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_chat),
                contentDescription = stringResource(R.string.app_name),
                modifier = Modifier.clickable {
                    onChatClick(item)
                }, // 크기, 패딩 등 지정 가능
                contentScale = ContentScale.Crop // 이미지 크기 조절 방식
            )
            Image(
                painter = painterResource(
                    id = if (item.bookmarkEntity.isBookmark) {
                        R.drawable.btn_star_pressed  // 북마크된 상태
                    } else {
                        R.drawable.btn_star_default   // 북마크 안된 상태
                    }
                ),
                contentDescription = stringResource(R.string.app_name),
                modifier = Modifier.clickable {
                    onBookmarkClick(item)
                }, // 크기, 패딩 등 지정 가능
                contentScale = ContentScale.Crop // 이미지 크기 조절 방식
            )
            Image(
                painter = painterResource(
                    id = if (item.likeEntity.isLike) {
                        R.drawable.btn_like_pressed  // 좋아요된 상태
                    } else {
                        R.drawable.btn_like_default   // 좋아요 안된 상태
                    }
                ),
                contentDescription = stringResource(R.string.app_name),
                modifier = Modifier.clickable {
                    onLikeClick(item)
                }, // 크기, 패딩 등 지정 가능
                contentScale = ContentScale.Crop // 이미지 크기 조절 방식
            )
        }

    }
}

@Preview(showBackground = true)
@Composable
fun PreviewBoardItemList() {
    val dummyItems = listOf(
        BoardEntity(
            boardMetaEntity = BoardMetaEntity(
                author = UserEntity(
                    email = "123",
                    nickname = "123",
                    image = null
                ),
                title = "안녕하세요",
                content = "테스트 합니다",
                images = null,
                createTime = Date(),
                editTime = null

            ),
            bookmarkEntity = BookmarkEntity(isBookmark = false),
            likeEntity = LikeEntity(isLike = false),
            likeCountEntity = LikeCountEntity(likeCount = 0)
        ),
        BoardEntity(
            boardMetaEntity = BoardMetaEntity(
                author = UserEntity(
                    email = "123",
                    nickname = "123",
                    image = null
                ), images = null, createTime = Date(), editTime = null
            ),
            bookmarkEntity = BookmarkEntity(isBookmark = false),
            likeEntity = LikeEntity(isLike = false),
            likeCountEntity = LikeCountEntity(likeCount = 0)
        )
    )

    BoardItemList(
        items = dummyItems,
        onItemClick = {},
        onRemove = {},
        onBookmarkClick = {},
        onLikeClick = {},
        onChatClick = {},
        onLoadMore = {},
        isLoadingMore = false
    )
}