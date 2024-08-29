package com.seungma.infratalk.domain.usecase

import com.seungma.infratalk.domain.board.repository.LikeDataRepository
import com.seungma.infratalk.domain.board.entity.LikeEntity
import com.seungma.infratalk.domain.comment.entity.CommentListEntity
import com.seungma.infratalk.domain.comment.usecase.AddCommentLikeUseCase
import com.seungma.infratalk.presenter.board.form.CommentLikeAddForm
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.jupiter.api.Assertions.assertTrue
import java.util.Date

class AddCommentLikeUseCaseTest {
    private val repository: LikeDataRepository = mockk()
    private lateinit var useCase: AddCommentLikeUseCase

    @Before
    fun setUp() {
        useCase = AddCommentLikeUseCase(repository)
    }

    @Test
    fun `코멘트 좋아요 추가 테스트`() = runTest {
        //given
        val currentTime = System.currentTimeMillis()
        val commentLikeAddForm: CommentLikeAddForm = mockk(relaxed = true) {
            every { commentAuthorEmail } returns "email"
            every { commentCreateTime } returns Date(currentTime)
        }
        val entity = CommentListEntity(
            commentList = listOf(mockk(relaxed = true) {
                every { likeEntity.isLike } returns false
                every { commentMetaEntity } returns mockk(relaxed = true) {
                    every { author.email } returns "email"
                    every { createTime } returns Date(currentTime)
                }
            })
        )

        coEvery { repository.addCommentLike(any()) } returns LikeEntity(isLike = true)
        coEvery { repository.loadCommentLikeCount(any()) } returns mockk()

        //when
        val result = useCase.invoke(commentLikeAddForm, mockk(), entity)

        //then
        assertTrue(result.commentList[0].likeEntity.isLike)

        coVerify(exactly = 1) { repository.addCommentLike(any()) }
        coVerify(exactly = 1) { repository.loadCommentLikeCount(any()) }
    }
}