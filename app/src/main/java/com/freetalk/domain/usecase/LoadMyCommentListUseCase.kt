package com.freetalk.domain.usecase

import android.util.Log
import com.freetalk.domain.entity.CommentEntity
import com.freetalk.domain.entity.CommentListEntity
import com.freetalk.domain.repository.BookmarkDataRepository
import com.freetalk.domain.repository.CommentDataRepository
import com.freetalk.domain.repository.LikeDataRepository
import com.freetalk.presenter.form.CommentBookmarkLoadForm
import com.freetalk.presenter.form.CommentLikeCountLoadForm
import com.freetalk.presenter.form.CommentLikeLoadForm
import com.freetalk.presenter.form.CommentMetaListLoadForm
import com.freetalk.presenter.form.MyCommentListLoadForm
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject

class LoadMyCommentListUseCase @Inject constructor(
    private val commentDataRepository: CommentDataRepository,
    private val bookmarkDataRepository: BookmarkDataRepository,
    private val likeDataRepository: LikeDataRepository,
) {
    suspend operator fun invoke(
        myCommentListLoadForm: MyCommentListLoadForm
    ): CommentListEntity = coroutineScope {

        val commentMetaListEntity =
            commentDataRepository.loadMyCommentList(myCommentListLoadForm = myCommentListLoadForm)

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