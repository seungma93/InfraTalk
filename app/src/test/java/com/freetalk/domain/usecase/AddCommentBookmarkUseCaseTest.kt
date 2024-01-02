package com.freetalk.domain.usecase

import com.freetalk.domain.entity.BookmarkEntity
import com.freetalk.domain.entity.CommentListEntity
import com.freetalk.domain.repository.BookmarkDataRepository
import com.freetalk.domain.repository.LikeDataRepository
import com.freetalk.presenter.form.CommentBookmarkAddForm
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.jupiter.api.Assertions.*
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