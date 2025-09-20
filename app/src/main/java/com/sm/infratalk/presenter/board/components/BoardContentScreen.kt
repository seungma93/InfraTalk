package com.sm.infratalk.presenter.board.components

import androidx.compose.runtime.Composable
import com.sm.infratalk.domain.board.entity.BoardEntity
import com.sm.infratalk.presenter.board.viewmodel.BoardContentViewModel

@Composable
fun BoardContentScreen(
    viewModel: BoardContentViewModel
) {

}

@Composable
fun BoardContent(
    onBookmarkClick: (BoardEntity) -> Unit,
    onLikeClick: (BoardEntity) -> Unit,
    onChatClick: (BoardEntity) -> Unit
) {

}