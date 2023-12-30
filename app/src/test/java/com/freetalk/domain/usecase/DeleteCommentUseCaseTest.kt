package com.freetalk.domain.usecase

import com.freetalk.domain.entity.CommentListEntity
import com.freetalk.domain.repository.BookmarkDataRepository
import com.freetalk.domain.repository.CommentDataRepository
import com.freetalk.domain.repository.LikeDataRepository
import com.freetalk.presenter.form.CommentDeleteForm
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

class DeleteCommentUseCaseTest {
    private val commentDataRepository: CommentDataRepository = mockk()
    private val bookmarkDataRepository: BookmarkDataRepository = mockk()
    private val likeDataRepository: LikeDataRepository = mockk()
    private lateinit var useCase: DeleteCommentUseCase

    @Before
    fun setUp() {
        useCase =
            DeleteCommentUseCase(commentDataRepository, bookmarkDataRepository, likeDataRepository)
    }

    @Test
    fun `코멘트 삭제 테스트`() = runTest {

        //given
        val currentTime = System.currentTimeMillis()
        val commentDeleteForm: CommentDeleteForm = mockk(relaxed = true) {
            every { commentAuthorEmail } returns "email"
            every { commentCreateTime } returns Date(currentTime)
        }
        val entity = CommentListEntity(
            commentList = listOf(
                mockk(relaxed = true) {
                    every { commentMetaEntity } returns mockk(relaxed = true) {
                        every { commentPrimaryKey } returns "email" + Date(currentTime)
                    }
                }
            )
        )

        coEvery { commentDataRepository.deleteComment(any()) } returns mockk()
        coEvery { bookmarkDataRepository.deleteCommentRelatedBookmarks(any()) } returns mockk()
        coEvery { likeDataRepository.deleteCommentRelatedLikes(any()) } returns mockk()

        //when
        val result = useCase.invoke(commentDeleteForm, mock(), mock(), entity)

        //then
        assert(result.commentList.isEmpty())

        coVerify(exactly = 1) { commentDataRepository.deleteComment(any()) }
        coVerify(exactly = 1) { bookmarkDataRepository.deleteCommentRelatedBookmarks(any()) }
        coVerify(exactly = 1) { likeDataRepository.deleteCommentRelatedLikes(any()) }
    }
}