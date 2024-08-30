package com.seungma.infratalk.domain.usecase

import com.seungma.infratalk.domain.board.repository.BookmarkDataRepository
import com.seungma.infratalk.domain.board.entity.BookmarkEntity
import com.seungma.infratalk.domain.board.entity.BoardEntity
import com.seungma.infratalk.domain.board.usecase.DeleteBoardContentBookmarkUseCase
import com.seungma.infratalk.presenter.board.form.BoardBookmarkDeleteForm
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.jupiter.api.Assertions.assertFalse
import java.util.Date

class DeleteBoardContentBookmarkUseCaseTest {
    private val repository: BookmarkDataRepository = mockk()
    private lateinit var useCase: DeleteBoardContentBookmarkUseCase

    @Before
    fun setUp() {
        useCase = DeleteBoardContentBookmarkUseCase(repository)
    }

    @Test
    fun `게시글 콘텐츠 북마크 삭제`() = runTest {
        //given
        val currentTime = System.currentTimeMillis()
        val boardBookmarkDeleteForm: BoardBookmarkDeleteForm = mockk(relaxed = true) {
            every { boardAuthorEmail } returns "email"
            every { boardCreateTime } returns Date(currentTime)
        }
        val boardEntity: BoardEntity = mockk(relaxed = true) {
            every { bookmarkEntity.isBookmark } returns true
            every { boardMetaEntity } returns mockk(relaxed = true) {
                every { author.email } returns "email"
                every { createTime } returns Date(currentTime)
            }

        }

        coEvery { repository.deleteBoardBookmark(any()) } returns BookmarkEntity(isBookmark = false)

        //when
        val result = useCase.invoke(boardBookmarkDeleteForm, boardEntity)

        //then
        assertFalse(result.bookmarkEntity.isBookmark)

        coVerify(exactly = 1) {repository.deleteBoardBookmark(any()) }
    }
}