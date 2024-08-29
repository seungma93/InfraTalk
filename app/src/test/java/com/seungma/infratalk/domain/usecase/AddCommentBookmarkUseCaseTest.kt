package com.seungma.infratalk.domain.usecase

import com.seungma.infratalk.domain.board.repository.BookmarkDataRepository
import com.seungma.infratalk.domain.board.entity.BookmarkEntity
import com.seungma.infratalk.domain.comment.entity.CommentListEntity
import com.seungma.infratalk.domain.comment.usecase.AddCommentBookmarkUseCase
import com.seungma.infratalk.presenter.board.form.CommentBookmarkAddForm
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.jupiter.api.Assertions.assertTrue
import java.util.Date

class AddCommentBookmarkUseCaseTest {
    private val repository: BookmarkDataRepository = mockk()
    private lateinit var useCase: AddCommentBookmarkUseCase

    @Before
    fun setUp() {
        useCase = AddCommentBookmarkUseCase(repository)
    }

    @Test
    fun `코멘트 북마크 추가 테스트`() = runTest {
        //given
        val currentTime = System.currentTimeMillis()
        val commentBookmarkAddForm: CommentBookmarkAddForm = mockk(relaxed = true) {
            every { commentAuthorEmail } returns "email"
            every { commentCreateTime } returns Date(currentTime)
        }
        val entity = CommentListEntity(
            commentList = listOf(
                mockk(relaxed = true) {
                    every { bookmarkEntity.isBookmark } returns false
                    every { commentMetaEntity } returns mockk(relaxed = true) {
                        every { author.email } returns "email"
                        every { createTime } returns Date(currentTime)
                    }
                }
            )
        )

        coEvery { repository.addCommentBookmark(any()) } returns BookmarkEntity(isBookmark = true)

        //when
        val result = useCase.invoke(commentBookmarkAddForm, entity)

        //then
        assertTrue(result.commentList[0].bookmarkEntity.isBookmark)

        coVerify(exactly = 1) { repository.addCommentBookmark(any()) }
    }


}