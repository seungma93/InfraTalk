package com.freetalk.domain.usecase

import com.freetalk.domain.entity.CommentEntity
import com.freetalk.domain.entity.CommentListEntity
import com.freetalk.domain.repository.BookmarkDataRepository
import com.freetalk.domain.repository.CommentDataRepository
import com.freetalk.domain.repository.LikeDataRepository
import com.freetalk.presenter.form.BoardRelatedAllCommentMetaListSelectForm
import com.freetalk.presenter.form.CommentBookmarkLoadForm
import com.freetalk.presenter.form.CommentLikeCountLoadForm
import com.freetalk.presenter.form.CommentLikeLoadForm
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

        CommentListEntity(
            commentList =
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

                val bookmarkEntity = asyncBookmark.await()
                val likeEntity = asyncLike.await()
                val likeCountEntity = asyncLikeCount.await()

                CommentEntity(
                    commentMetaEntity = it,
                    bookmarkEntity = bookmarkEntity,
                    likeEntity = likeEntity,
                    likeCountEntity = likeCountEntity
                )
            }
        )
    }
}