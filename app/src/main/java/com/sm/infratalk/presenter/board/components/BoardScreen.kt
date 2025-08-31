package com.sm.infratalk.presenter.board.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sm.infratalk.domain.board.entity.BoardEntity


// 리스트 전체
@Composable
fun BoardItemList(
    items: List<BoardEntity>,
    onItemClick: (BoardEntity) -> Unit,
    onRemove: (String) -> Unit,
    onLoadMore: () -> Unit
) {
    LazyColumn {

    }
}

// 아이템
@Composable
fun BoardItemRow(item: BoardEntity, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick).padding(16.dp)
    ) {
        // TODO("작성자, 날짜")

        // TODO("제목, 내용")
            Text(modifier = Modifier.size(20.dp), text = item.boardMetaEntity.title)
            Text(modifier = Modifier.size(20.dp), text = item.boardMetaEntity.content)
        // TODO("버튼")
    }
}

