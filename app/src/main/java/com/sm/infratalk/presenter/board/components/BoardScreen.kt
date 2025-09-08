package com.sm.infratalk.presenter.board.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.google.android.gms.common.util.CollectionUtils.listOf
import com.sm.infratalk.R
import com.sm.infratalk.domain.board.entity.BoardEntity
import com.sm.infratalk.domain.board.entity.BoardMetaEntity
import com.sm.infratalk.domain.board.entity.BookmarkEntity
import com.sm.infratalk.domain.board.entity.LikeCountEntity
import com.sm.infratalk.domain.board.entity.LikeEntity
import com.sm.infratalk.domain.user.entity.UserEntity
import com.sm.infratalk.presenter.board.viewmodel.BoardViewModel
import java.util.Date



@Composable
fun BoardScreen(
    viewModel: BoardViewModel
) {

    var boardItems by remember { mutableStateOf<List<BoardEntity>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        isLoading = true

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
    onRemove: (String) -> Unit,
    onLoadMore: () -> Unit
) {
    LazyColumn {
        items(items) { item ->
            BoardItemRow(
                item = item,
                onClick = onItemClick,
                onBookmarkClick = onBookmarkClick,
                onLikeClick = onLikeClick,
                onChatClick = onChatClick
            )
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
                painter = painterResource(id = R.drawable.btn_like_default),
                contentDescription = stringResource(R.string.app_name),
                modifier = Modifier.clickable {
                    onLikeClick(item)
                }, // 크기, 패딩 등 지정 가능
                contentScale = ContentScale.Crop // 이미지 크기 조절 방식
            )
            Image(
                painter = painterResource(id = R.drawable.btn_like_default),
                contentDescription = stringResource(R.string.app_name),
                modifier = Modifier.clickable {
                    onBookmarkClick(item)
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
        onLoadMore = {}
    )
}