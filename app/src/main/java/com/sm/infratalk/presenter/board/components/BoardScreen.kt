package com.sm.infratalk.presenter.board.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sm.infratalk.R
import com.sm.infratalk.domain.board.entity.BoardEntity
import com.sm.infratalk.domain.board.entity.BoardMetaEntity
import com.sm.infratalk.domain.board.entity.BookmarkEntity
import com.sm.infratalk.domain.board.entity.LikeCountEntity
import com.sm.infratalk.domain.board.entity.LikeEntity
import com.sm.infratalk.domain.user.entity.UserEntity
import java.util.Date


// 리스트 전체
@Composable
fun BoardItemList(
    items: List<BoardEntity>,
    onItemClick: (BoardEntity) -> Unit,
    onRemove: (String) -> Unit,
    onLoadMore: () -> Unit
) {
    LazyColumn {
        items(items) { item ->
            BoardItemRow(item = item, onClick = { onItemClick(item) })
        }
    }
}

// 아이템
@Composable
fun BoardItemRow(item: BoardEntity, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick).padding(16.dp)
    ) {
        // TODO("작성자, 날짜")
        Row(modifier = Modifier.fillMaxWidth()) {
            Image(
                painter = painterResource(id = R.drawable.ic_avatar),
                contentDescription = stringResource(R.string.app_name),
                modifier = Modifier, // 크기, 패딩 등 지정 가능
                contentScale = ContentScale.Crop // 이미지 크기 조절 방식
            )
            Column {
                Text(modifier = Modifier.size(20.dp), text = item.boardMetaEntity.author.nickname)
                Text(modifier = Modifier.size(20.dp), text = item.boardMetaEntity.createTime.toString())
            }
        }

        // TODO("제목, 내용")
            Text(modifier = Modifier.size(20.dp), text = item.boardMetaEntity.title)
            Text(modifier = Modifier.size(20.dp), text = item.boardMetaEntity.content)
        // TODO("버튼")
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
                    nickname = "",
                    image = null
                ), title = "123", content = "123", images = null, createTime = Date(), editTime = null

            ),
            bookmarkEntity = BookmarkEntity(isBookmark = false),
            likeEntity = LikeEntity(isLike = false),
            likeCountEntity = LikeCountEntity(likeCount = 0)
        ),
        BoardEntity(
            boardMetaEntity = BoardMetaEntity(
                author = UserEntity(
                    email = "123",
                    nickname = "",
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
        onLoadMore = {}
    )
}