package com.sm.infratalk.presenter.board.components

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.google.android.gms.common.util.CollectionUtils
import com.sm.infratalk.R
import com.sm.infratalk.domain.board.entity.BoardEntity
import com.sm.infratalk.domain.comment.entity.CommentEntity
import com.sm.infratalk.presenter.board.form.BoardBookmarkAddForm
import com.sm.infratalk.presenter.board.form.BoardBookmarkDeleteForm
import com.sm.infratalk.presenter.board.form.BoardLikeAddForm
import com.sm.infratalk.presenter.board.form.BoardLikeCountLoadForm
import com.sm.infratalk.presenter.board.form.BoardLikeDeleteForm
import com.sm.infratalk.presenter.board.viewmodel.BoardContentViewModel
import com.sm.infratalk.presenter.chat.form.ChatRoomCheckForm
import com.sm.infratalk.presenter.chat.form.ChatRoomCreateForm
import kotlinx.coroutines.launch
import org.w3c.dom.Text

@Composable
fun BoardContentScreen(
    viewModel: BoardContentViewModel,
    boardEntity: BoardEntity
) {

    val coroutineScope = rememberCoroutineScope()


    Column {

        // 게시글 상셍
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


    }


}

@Composable
fun BoardContent(
    item: BoardEntity,
    onBookmarkClick: (BoardEntity) -> Unit,
    onLikeClick: (BoardEntity) -> Unit,
    onChatClick: (BoardEntity) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
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

) {

}

@Composable
fun BoardCommentItemRow(
    item: CommentEntity,
) {
    
}