package com.freetalk.domain.usecase

import com.freetalk.domain.entity.BoardListEntity
import com.freetalk.domain.entity.BookmarkEntity
import com.freetalk.domain.entity.CommentListEntity
import com.freetalk.domain.entity.LikeEntity
import com.freetalk.domain.repository.BookmarkDataRepository
import com.freetalk.domain.repository.LikeDataRepository
import com.freetalk.presenter.form.BoardBookmarkDeleteForm
import com.freetalk.presenter.form.CommentLikeDeleteForm
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.jupiter.api.Assertions.*
import java.util.Date

class DeleteBoardBookmarkUseCaseTest {
    private val repository: BookmarkDataRepository = mockk()
    private lateinit var useCase: DeleteBoardBookmarkUseCase

    @Before
    fun setUp() {
        useCase = DeleteBoardBookmarkUseCase(repository)
    }

    @Test
    fun `게시글의 북마크 삭제 테스트`() = runTest {
        //given
        val currentTime = System.currentTimeMillis()
        val boardBookmarkDeleteForm: BoardBookmarkDeleteForm = mockk(relaxed = true) {
            every { boardAuthorEmail } returns "email"
            every { boardCreateTime } returns Date(currentTime)
        }
        val entity = BoardListEntity(
            boardList = listOf(
                mockk(relaxed = true) {
                    every { bookmarkEntity.isBookmark } returns true
                    every { boardMetaEntity } returns mockk(relaxed = true) {
                        every { author.email } returns "email"
                        every { createTime } returns Date(currentTime)
                    }
                }
            )
        )

        coEvery { repository.deleteBoardBookmark(any()) } returns BookmarkEntity(isBookmark = false)

        //when
        val result = useCase.invoke(boardBookmarkDeleteForm, entity)

        //then
        assertFalse(result.boardList[0].bookmarkEntity.isBookmark)

        coVerify(exactly = 1) { repository.deleteBoardBookmark(any()) }


    }

}