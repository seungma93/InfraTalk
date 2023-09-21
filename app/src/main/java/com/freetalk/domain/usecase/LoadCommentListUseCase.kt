package com.freetalk.domain.usecase

import com.freetalk.domain.entity.CommentEntity
import com.freetalk.domain.entity.CommentListEntity
import com.freetalk.domain.repository.BookmarkDataRepository
import com.freetalk.domain.repository.CommentDataRepository
import com.freetalk.domain.repository.LikeDataRepository
import com.freetalk.presenter.form.CommentBookmarkLoadForm
import com.freetalk.presenter.form.CommentLikeCountLoadForm
import com.freetalk.presenter.form.CommentLikeLoadForm
import com.freetalk.presenter.form.CommentMetaListLoadForm
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject

class LoadCommentListUseCase @Inject constructor(
    private val commentDataRepository: CommentDataRepository,
    private val bookmarkDataRepository: BookmarkDataRepository,
    private val likeDataRepository: LikeDataRepository,
) {
    suspend operator fun invoke(
        commentMetaListLoadForm: CommentMetaListLoadForm
    ): CommentListEntity = coroutineScope {

        val commentMetaListEntity =
            commentDataRepository.loadCommentMetaList(commentMetaListLoadForm = commentMetaListLoadForm)

        CommentListEntity(
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