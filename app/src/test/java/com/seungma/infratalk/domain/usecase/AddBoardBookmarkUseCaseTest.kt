package com.seungma.infratalk.domain.usecase

import com.seungma.infratalk.domain.board.repository.BookmarkDataRepository
import com.seungma.infratalk.domain.board.entity.BookmarkEntity
import com.seungma.infratalk.domain.board.entity.BoardListEntity
import com.seungma.infratalk.domain.board.usecase.AddBoardBookmarkUseCase
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

class AddBoardBookmarkUseCaseTest {
    private val repository: BookmarkDataRepository = mockk()
    private lateinit var useCase: AddBoardBookmarkUseCase

    @Before
    fun setUp() {
        useCase = AddBoardBookmarkUseCase(repository)
    }

    @Test
    fun `게시판 북마크 추가 테스트`() = runTest {
        //given
        val currentTime = System.currentTimeMillis()
        val boardBookmarkAddForm: BoardBookmarkAddForm = mockk(relaxed = true) {
            every { boardAuthorEmail } returns "email"
            every { boardCreateTime } returns Date(currentTime)
        }
        val entity = BoardListEntity(
            boardList = listOf(
                mockk(relaxed = true) {
                    every { bookmarkEntity.isBookmark } returns false
                    every { boardMetaEntity } returns mockk(relaxed = true) {
                        every { author.email } returns "email"
                        every { createTime } returns Date(currentTime)
                    }
                }
            )
        )

        coEvery { repository.addBoardBookmark(any()) } returns BookmarkEntity(isBookmark = true)

        //when
        val result = useCase.invoke(boardBookmarkAddForm, entity)

        //then
        assertTrue(result.boardList[0].bookmarkEntity.isBookmark)

        coVerify(exactly = 1) { repository.addBoardBookmark(any()) }

    }
}