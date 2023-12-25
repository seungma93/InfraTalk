package com.freetalk.domain.usecase

import com.freetalk.domain.entity.CommentListEntity
import com.freetalk.domain.entity.LikeEntity
import com.freetalk.domain.repository.LikeDataRepository
import com.freetalk.presenter.form.CommentLikeDeleteForm
import com.nhaarman.mockitokotlin2.mock
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.jupiter.api.Assertions.*
import java.util.Date

class DeleteCommentLikeUseCaseTest {
    private val repository: LikeDataRepository = mockk()
    private lateinit var useCase: DeleteCommentLikeUseCase

    @Before
    fun setUp() {
        useCase = DeleteCommentLikeUseCase(repository)
    }

    @Test
    fun `코멘트의 좋아요 삭제 테스트`() = runTest {
        //given
        val currentTime = System.currentTimeMillis()
        val commentLikeDeleteForm: CommentLikeDeleteForm = mockk(relaxed = true) {
            every { commentAuthorEmail } returns "email"
            every { commentCreateTime } returns Date(currentTime)
        }
        val entity = CommentListEntity(
            commentList = listOf(
                mockk(relaxed = true) {
                    every { likeEntity.isLike } returns true
                    every { commentMetaEntity } returns mockk(relaxed = true) {
                        every { author.email } returns "email"
                        every { createTime } returns Date(currentTime)
                    }
                }
            )
        )

        coEvery { repository.deleteCommentLike(any()) } returns LikeEntity(isLike = false)
        coEvery { repository.loadCommentLikeCount(any()) } returns mockk()

        //when
        val result = useCase.invoke(commentLikeDeleteForm, mockk(), entity)

        //then
        assertFalse(result.commentList[0].likeEntity.isLike)

        coVerify(exactly = 1) { repository.deleteCommentLike(any()) }
        coVerify(exactly = 1) { repository.loadCommentLikeCount(any()) }

    }

}