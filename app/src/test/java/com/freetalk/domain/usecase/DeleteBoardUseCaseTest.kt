package com.freetalk.domain.usecase

import com.freetalk.domain.entity.BoardListEntity
import com.freetalk.domain.repository.BoardDataRepository
import com.freetalk.domain.repository.BookmarkDataRepository
import com.freetalk.domain.repository.CommentDataRepository
import com.freetalk.domain.repository.LikeDataRepository
import com.freetalk.presenter.form.BoardBookmarksDeleteForm
import com.freetalk.presenter.form.BoardDeleteForm
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

class DeleteBoardUseCaseTest {
    private val boardDataRepository: BoardDataRepository = mockk()
    private val bookmarkDataRepository: BookmarkDataRepository = mockk()
    private val likeDataRepository: LikeDataRepository = mockk()
    private val commentDataRepository: CommentDataRepository = mockk()
    private lateinit var useCase: DeleteBoardUseCase

    @Before
    fun setUp() {
        useCase = DeleteBoardUseCase(
            boardDataRepository, bookmarkDataRepository, likeDataRepository, commentDataRepository
        )
    }

    @Test
    fun `테스트`() = runTest {
        // given
        val current = System.currentTimeMillis()
        val boardDeleteForm: BoardDeleteForm = mockk(relaxed = true) {
            every { boardAuthorEmail } returns "email"
            every { boardCreateTime } returns Date(current)
        }
        val entity = BoardListEntity(
            boardList = listOf(
                mockk(relaxed = true) {
                    every { boardMetaEntity } returns mockk(relaxed = true) {
                        every { boardPrimaryKey } returns "email" + Date(current)
                    }
                },
                mockk(relaxed = true)
            )
        )

        coEvery {
            commentDataRepository.loadBoardRelatedAllCommentMetaList(any())
        } returns mockk {
            every { commentMetaList } returns emptyList()
        }
        coEvery {
            boardDataRepository.deleteBoard(any())
        } returns mockk()
        coEvery {
            bookmarkDataRepository.deleteBoardBookmarks(any())
        } returns mockk()
        coEvery {
            likeDataRepository.deleteBoardLikes(any())
        } returns mockk()

        // when
        val result = useCase.invoke(boardDeleteForm, mockk(), mockk(), entity)

        // then
        assert(result.boardList.isNotEmpty())
        assert(result.boardList.size == 1)

        coVerify(exactly = 1) {
            commentDataRepository.loadBoardRelatedAllCommentMetaList(any())
        }
        coVerify(exactly = 1) {
            boardDataRepository.deleteBoard(any())
        }
        coVerify(exactly = 1) {
            bookmarkDataRepository.deleteBoardBookmarks(any())
        }
        coVerify(exactly = 1) {
            likeDataRepository.deleteBoardLikes(any())
        }

        coVerify(exactly = 0) {
            commentDataRepository.deleteComment(any())
        }
        coVerify(exactly = 0) {
            likeDataRepository.deleteCommentRelatedLikes(any())
        }
        coVerify(exactly = 0) {
            bookmarkDataRepository.deleteCommentRelatedBookmarks(any())
        }
    }
}