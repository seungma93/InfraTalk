package com.freetalk.domain.usecase

import com.freetalk.domain.entity.BoardListEntity
import com.freetalk.domain.entity.BookmarkEntity
import com.freetalk.domain.entity.CommentListEntity
import com.freetalk.domain.repository.BookmarkDataRepository
import com.freetalk.presenter.form.CommentBookmarkDeleteForm
import io.mockk.MockKSettings
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.jupiter.api.Assertions.*
import java.util.Date

class DeleteCommentBookmarkUseCaseTest {
    private val repository: BookmarkDataRepository = mockk()
    private lateinit var useCase: DeleteCommentBookmarkUseCase


    @Before
    fun setUp() {
        useCase = DeleteCommentBookmarkUseCase(repository)
    }

    @Test
    fun `코멘트의 북마크 삭제 테스트`() = runTest {
        //given
        val currentTime = System.currentTimeMillis()
        val commentBookmarkDeleteForm: CommentBookmarkDeleteForm = mockk(relaxed = true) {
            every { commentAuthorEmail } returns "email"
            every { commentCreateTime } returns Date(currentTime)
        }
        val entity = CommentListEntity(
            commentList = listOf(
                mockk(relaxed = true) {
                    every { bookmarkEntity.isBookmark } returns true
                    every { commentMetaEntity } returns mockk(relaxed = true) {
                        every { author.email } returns "email"
                        every { createTime} returns Date(currentTime)
                    }
                }
            )
        )

        coEvery {
            repository.deleteCommentBookmark(any())
        } returns BookmarkEntity(isBookmark = false)

        //when
        val result = useCase.invoke(commentBookmarkDeleteForm, entity)

        //then
        assertFalse(result.commentList[0].bookmarkEntity.isBookmark)

        coVerify(exactly = 1) {
            repository.deleteCommentBookmark(any())
        }
    }
}