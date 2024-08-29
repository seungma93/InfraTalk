package com.seungma.infratalk.domain.usecase

import com.seungma.infratalk.domain.board.repository.BookmarkDataRepository
import com.seungma.infratalk.domain.board.entity.BookmarkEntity
import com.seungma.infratalk.domain.board.entity.BoardEntity
import com.seungma.infratalk.domain.board.usecase.AddBoardContentBookmarkUseCase
import com.seungma.infratalk.presenter.board.form.BoardBookmarkAddForm
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.jupiter.api.Assertions.assertTrue
import java.util.Date

class AddBoardContentBookmarkUseCaseTest {
    private val repository: BookmarkDataRepository = mockk()
    private lateinit var useCase: AddBoardContentBookmarkUseCase

    @Before
    fun setUp() {
        useCase = AddBoardContentBookmarkUseCase(repository)
    }

    @Test
    fun `게시글 컨텐츠 북마크 추가 테스트`() = runTest {
        //given
        val currentTime = System.currentTimeMillis()
        val boardBookmarkAddForm: BoardBookmarkAddForm = mockk(relaxed = true) {
            every { boardAuthorEmail } returns "email"
            every { boardCreateTime } returns Date(currentTime)
        }
        val boardEntity: BoardEntity = mockk(relaxed = true) {
            every { bookmarkEntity.isBookmark } returns false
            every { boardMetaEntity } returns mockk(relaxed = true) {
                every { author.email } returns "email"
                every { createTime } returns Date(currentTime)
            }
        }

        coEvery { repository.addBoardBookmark(boardBookmarkAddForm) } returns BookmarkEntity(isBookmark = true)

        //when
        val result = useCase.invoke(boardBookmarkAddForm, boardEntity)

        //then

        assertTrue(result.bookmarkEntity.isBookmark)

        coVerify(exactly = 1) { repository.addBoardBookmark(any()) }
    }
}