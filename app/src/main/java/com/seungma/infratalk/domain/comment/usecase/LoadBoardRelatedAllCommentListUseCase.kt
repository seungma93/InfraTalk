package com.seungma.infratalk.domain.comment.usecase

import com.seungma.infratalk.domain.board.repository.BookmarkDataRepository
import com.seungma.infratalk.domain.board.repository.LikeDataRepository
import com.seungma.infratalk.domain.comment.entity.CommentEntity
import com.seungma.infratalk.domain.comment.entity.CommentListEntity
import com.seungma.infratalk.domain.comment.repository.CommentDataRepository
import com.seungma.infratalk.presenter.board.form.BoardRelatedAllCommentMetaListSelectForm
import com.seungma.infratalk.presenter.board.form.CommentBookmarkLoadForm
import com.seungma.infratalk.presenter.board.form.CommentLikeCountLoadForm
import com.seungma.infratalk.presenter.board.form.CommentLikeLoadForm
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject

class LoadBoardRelatedAllCommentListUseCase @Inject constructor(
    private val commentDataRepository: CommentDataRepository,
    private val bookmarkDataRepository: BookmarkDataRepository,
    private val likeDataRepository: LikeDataRepository,
) {
    suspend operator fun invoke(
        boardRelatedAllCommentMetaListSelectForm: BoardRelatedAllCommentMetaListSelectForm,
    ): CommentListEntity = coroutineScope {

        val commentMetaListEntity = commentDataRepository.loadBoardRelatedAllCommentMetaList(
            boardRelatedAllCommentMetaListSelectForm = boardRelatedAllCommentMetaListSelectForm
        )
        commentMetaListEntity.commentMetaList.map {
            val asyncBookmark =
                async {
                    bookmarkDataRepository.loadCommentBookmark(
                        commentBookmarkLoadForm = CommentBookmarkLoadForm(
                            commentAuthorEmail = it.author.email,
                            commentCreateTime = it.createTime
                        )
                    )
                }
            val asyncLike =
                async {
                    likeDataRepository.loadCommentLike(
                        commentLikeLoadForm = CommentLikeLoadForm(
                            commentAuthorEmail = it.author.email,
                            commentCreateTime = it.createTime
                        )
                    )
                }
            val asyncLikeCount =
                async {
                    likeDataRepository.loadCommentLikeCount(
                        commentLikeCountLoadForm = CommentLikeCountLoadForm(
                            commentAuthorEmail = it.author.email,
                            commentCreateTime = it.createTime
                        )
                    )
                }
            it to Triple(asyncBookmark, asyncLike, asyncLikeCount)
        }.map { (commentMeta, deferred) ->
            val asyncBookmark = deferred.first
            val asyncLike = deferred.second
            val asyncLikeCount = deferred.third
            CommentEntity(
                commentMetaEntity = commentMeta,
                bookmarkEntity = asyncBookmark.await(),
                likeEntity = asyncLike.await(),
                likeCountEntity = asyncLikeCount.await()
            )
        }.let {
            CommentListEntity(commentList = it)
        }
    }
}