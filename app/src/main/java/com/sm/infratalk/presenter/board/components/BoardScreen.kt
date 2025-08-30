package com.sm.infratalk.presenter.board.components

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
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



