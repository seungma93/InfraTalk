package com.seungma.infratalk.domain.usecase

import com.seungma.infratalk.domain.BookmarkDataRepository
import com.seungma.infratalk.domain.LikeDataRepository
import com.seungma.infratalk.domain.board.entity.BoardListEntity
import com.seungma.infratalk.domain.board.repository.BoardDataRepository
import com.seungma.infratalk.domain.board.usecase.DeleteBoardUseCase
import com.seungma.infratalk.domain.comment.repository.CommentDataRepository
import com.seungma.infratalk.presenter.board.form.BoardDeleteForm
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
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
    fun `게시글 삭제 테스트`() = runTest {
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
                }
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
        assert(result.boardList.isEmpty())


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