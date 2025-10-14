package com.sm.infratalk.presenter.board.components

import android.net.Uri
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.rememberAsyncImagePainter
import com.sm.infratalk.R
import com.sm.infratalk.domain.board.entity.BoardEntity
import com.sm.infratalk.domain.comment.entity.CommentEntity
import com.sm.infratalk.domain.user.entity.UserEntity
import com.sm.infratalk.presenter.board.form.BoardBookmarkAddForm
import com.sm.infratalk.presenter.board.form.BoardBookmarkDeleteForm
import com.sm.infratalk.presenter.board.form.BoardLikeAddForm
import com.sm.infratalk.presenter.board.form.BoardLikeCountLoadForm
import com.sm.infratalk.presenter.board.form.BoardLikeDeleteForm
import com.sm.infratalk.presenter.board.form.CommentBookmarkAddForm
import com.sm.infratalk.presenter.board.form.CommentBookmarkDeleteForm
import com.sm.infratalk.presenter.board.form.CommentLikeAddForm
import com.sm.infratalk.presenter.board.form.CommentLikeCountLoadForm
import com.sm.infratalk.presenter.board.form.CommentLikeDeleteForm
import com.sm.infratalk.presenter.board.form.CommentMetaListLoadForm
import com.sm.infratalk.presenter.board.viewmodel.BoardContentViewModel
import kotlinx.coroutines.launch

@Composable
fun BoardContentScreen(
    viewModel: BoardContentViewModel
) {

    val coroutineScope = rememberCoroutineScope()
    val viewState by viewModel.viewState.collectAsStateWithLifecycle()
    var isLoading by remember { mutableStateOf(false) }
    var isLoadingMore by remember { mutableStateOf(false) }
    var userEntity by remember { mutableStateOf<UserEntity?>(null) }

    LaunchedEffect(Unit) {
        userEntity = viewModel.getUserMe()
    }

    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }

    } else {
        Column {
            // 게시글 상세
            viewState.boardEntity?.let { boardEntity ->
                BoardContent(
                    item = boardEntity,
                    onBookmarkClick = {
                        when (it.bookmarkEntity.isBookmark) {
                            true -> {
                                coroutineScope.launch {
                                    viewModel.deleteBoardContentBookmark(
                                        BoardBookmarkDeleteForm(
                                            boardAuthorEmail = it.boardMetaEntity.author.email,
                                            boardCreateTime = it.boardMetaEntity.createTime
                                        )
                                    )
                                }
                            }

                            false -> {
                                coroutineScope.launch {
                                    viewModel.addBoardContentBookmark(
                                        BoardBookmarkAddForm(
                                            boardAuthorEmail = it.boardMetaEntity.author.email,
                                            boardCreateTime = it.boardMetaEntity.createTime
                                        )
                                    )
                                }
                            }
                        }
                    },
                    onLikeClick = {
                        when (it.likeEntity.isLike) {
                            true -> {
                                coroutineScope.launch {
                                    viewModel.deleteBoardContentLike(
                                        boardLikeDeleteForm = BoardLikeDeleteForm(
                                            boardAuthorEmail = it.boardMetaEntity.author.email,
                                            boardCreateTime = it.boardMetaEntity.createTime
                                        ),
                                        boardLikeCountLoadForm = BoardLikeCountLoadForm(
                                            boardAuthorEmail = it.boardMetaEntity.author.email,
                                            boardCreateTime = it.boardMetaEntity.createTime
                                        )
                                    )
                                }
                            }

                            false -> {
                                coroutineScope.launch {
                                    viewModel.addBoardContentLike(
                                        boardLikeAddForm = BoardLikeAddForm(
                                            boardAuthorEmail = it.boardMetaEntity.author.email,
                                            boardCreateTime = it.boardMetaEntity.createTime
                                        ),
                                        boardLikeCountLoadForm = BoardLikeCountLoadForm(
                                            boardAuthorEmail = it.boardMetaEntity.author.email,
                                            boardCreateTime = it.boardMetaEntity.createTime
                                        )
                                    )
                                }
                            }
                        }
                    },
                    onChatClick = {
                    }
                )
                // 댓글
                viewState.commentListEntity?.let { commentListEntity ->

                    BoardCommentList (
                        items = commentListEntity.commentList,
                        onBookmarkClick = { commentEntity ->
                            commentEntity.apply {
                                when (bookmarkEntity.isBookmark) {
                                    true -> {
                                        coroutineScope.launch {
                                            viewModel.deleteCommentBookmark(
                                                commentBookmarkDeleteForm = CommentBookmarkDeleteForm(
                                                    commentAuthorEmail = commentMetaEntity.author.email,
                                                    commentCreateTime = commentMetaEntity.createTime
                                                )
                                            )
                                        }

                                    }

                                    false -> {
                                        coroutineScope.launch {
                                            viewModel.addCommentBookmark(
                                                commentBookmarkAddForm = CommentBookmarkAddForm(
                                                    commentAuthorEmail = commentMetaEntity.author.email,
                                                    commentCreateTime = commentMetaEntity.createTime
                                                )
                                            )
                                        }
                                    }
                                }
                            }
                        },
                        onLikeClick = { commentEntity ->
                            commentEntity.apply {
                                when (likeEntity.isLike) {
                                    true -> {
                                        coroutineScope.launch {
                                            viewModel.deleteCommentLike(
                                                commentLikeDeleteForm = CommentLikeDeleteForm(
                                                    commentAuthorEmail = commentMetaEntity.author.email,
                                                    commentCreateTime = commentMetaEntity.createTime
                                                ), commentLikeCountLoadForm = CommentLikeCountLoadForm(
                                                    commentAuthorEmail = commentMetaEntity.author.email,
                                                    commentCreateTime = commentMetaEntity.createTime
                                                )
                                            )
                                        }
                                    }

                                    false -> {
                                        coroutineScope.launch {
                                            viewModel.addCommentLike(
                                                commentLikeAddForm = CommentLikeAddForm(
                                                    commentAuthorEmail = commentMetaEntity.author.email,
                                                    commentCreateTime = commentMetaEntity.createTime
                                                ), commentLikeCountLoadForm = CommentLikeCountLoadForm(
                                                    commentAuthorEmail = commentMetaEntity.author.email,
                                                    commentCreateTime = commentMetaEntity.createTime
                                                )
                                            )
                                        }
                                    }
                                }
                            }
                        },
                        onLoadMore = {
                            // 중복 호출 방지
                            if (!isLoadingMore) {
                                isLoadingMore = true
                                coroutineScope.launch {
                                    try {
                                        Log.d("BoardScreen", "더보기 로드 시작")
                                        viewModel.loadCommentList(commentMetaListLoadForm = CommentMetaListLoadForm(
                                            boardAuthorEmail = boardEntity.boardMetaEntity.author.email,
                                            boardCreateTime = boardEntity.boardMetaEntity.createTime,
                                            reload = false,
                                        ))
                                        Log.d("BoardScreen", "더보기 로드 완료")
                                    } catch (e: Exception) {
                                        Log.e("BoardScreen", "더보기 로드 실패", e)
                                    } finally {
                                        isLoadingMore = false
                                    }
                                }
                            }
                        },
                        isLoadingMore = isLoadingMore,
                        userEntity = userEntity ?: throw Exception("유저 정보가 없습니다.")

                    )
                }
            }

        }
    }
    



}

@Composable
fun BoardContent(
    item: BoardEntity,
    onBookmarkClick: (BoardEntity) -> Unit,
    onLikeClick: (BoardEntity) -> Unit,
    onChatClick: (BoardEntity) -> Unit
) {
    var showDialog by remember { mutableStateOf(false)}

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // TODO("작성자, 날짜")
        Row(modifier = Modifier.fillMaxWidth()) {
            Image(
                painter = item.boardMetaEntity.images?.let {
                    if(it.successUris.isNotEmpty()) {
                        rememberAsyncImagePainter(it.successUris)
                    } else {
                        painterResource(id = R.drawable.ic_avatar)
                    }
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
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .clickable {
                    showDialog = true
                }, // 리스트 자체의 높이
            horizontalArrangement = Arrangement.spacedBy(8.dp), // 아이템 간격
            contentPadding = PaddingValues(horizontal = 16.dp),

        ) {
            item.boardMetaEntity.images?.successUris?.let { items ->
                items(items = items) { item ->

                    ImageItemRow(
                        item = item,

                    )
                }
            }

        }

        if(showDialog) {

            item.boardMetaEntity.images?.successUris?.let {
                // 다이어로그 호출
                ImageSliderDialog(images = it, initialIndex = 0, onDismiss = {
                    showDialog = false
                })
            }

        }

        Text(modifier = Modifier.fillMaxWidth(), text = item.boardMetaEntity.content)

        // TODO("버튼")

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp, horizontal = 10.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            /*Image(
                painter = painterResource(id = R.drawable.ic_chat),
                contentDescription = stringResource(R.string.app_name),
                modifier = Modifier.clickable {
                    onChatClick(item)
                }, // 크기, 패딩 등 지정 가능
                contentScale = ContentScale.Crop // 이미지 크기 조절 방식
            )*/
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

@Composable
fun BoardCommentList(
    items: List<CommentEntity>,
    onBookmarkClick: (CommentEntity) -> Unit,
    onLikeClick: (CommentEntity) -> Unit,
    onLoadMore: () -> Unit,
    isLoadingMore: Boolean = false,
    userEntity: UserEntity
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
                        Log.d("BoardScreen", "스크롤 감지: 더보기 호출 (${lastVisibleItem.index}/${totalItems})")
                        onLoadMore()
                    }
                }
            }
    }

    LazyColumn(
        state = listState  // 스크롤 상태 연결
    ) {
        items(items) { item ->
            BoardCommentItemRow(
                item = item,
                onBookmarkClick = onBookmarkClick,
                onLikeClick = onLikeClick,
                userEntity = userEntity

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

@Composable
fun BoardCommentItemRow(
    item: CommentEntity,
    onBookmarkClick: (CommentEntity) -> Unit,
    onLikeClick: (CommentEntity) -> Unit,
    userEntity: UserEntity
) {
    Column {
        Row {
            Text(modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp),
                text = item.commentMetaEntity.author.nickname)
            if(userEntity.email == item.commentMetaEntity.author.email){
                Image(
                    painter = painterResource(
                        id = R.drawable.ic_clear
                    ),
                    contentDescription = stringResource(R.string.app_name),
                    modifier = Modifier.clickable {
                        onBookmarkClick(item)
                    }, // 크기, 패딩 등 지정 가능
                    contentScale = ContentScale.Crop // 이미지 크기 조절 방식
                )
            }
        }

        Text(modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp),
            text = item.commentMetaEntity.content)
        Row {
            Text(modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp),
                text = item.commentMetaEntity.createTime.toString())
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
            Text(modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp),
                text = item.likeCountEntity.likeCount.toString()
            )
        }
    }
}


@Composable
fun ImageItemRow(
    item: Uri
) {
    Box(modifier = Modifier) {
        Image(
            painter = rememberAsyncImagePainter(model = item),
            contentDescription = "selected image",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f) // 정사각형 형태 (원하면 제거 가능)
        )
    }
}

@Composable
fun ImageSliderDialog(
    images: List<Uri>,
    initialIndex: Int = 0,
    onDismiss: () -> Unit
) {
    var currentIndex by remember { mutableStateOf(initialIndex) }
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = initialIndex)

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.9f))
        ) {
            // 닫기 버튼
            IconButton(
                onClick = onDismiss,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_clear),
                    contentDescription = "닫기",
                    tint = Color.White
                )
            }

            // 이미지 카운터
            Text(
                text = "${currentIndex + 1} / ${images.size}",
                color = Color.White,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(16.dp)
            )

            // 이미지 슬라이더
            LazyRow(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                items(images.size) { index ->
                    Image(
                        painter = rememberAsyncImagePainter(images[index]),
                        contentDescription = "이미지 ${index + 1}",
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f),
                        contentScale = ContentScale.Fit
                    )
                }
            }

            // 인디케이터 (작은 점들)
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                repeat(images.size) { index ->
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(
                                color = if (index == currentIndex) Color.White else Color.Gray,
                                shape = CircleShape
                            )
                            .padding(horizontal = 4.dp)
                    )
                }
            }
        }
    }
}
