package com.seungma.infratalk.domain.usecase

import com.seungma.infratalk.domain.board.repository.LikeDataRepository
import com.seungma.infratalk.domain.board.entity.LikeEntity
import com.seungma.infratalk.domain.board.entity.BoardEntity
import com.seungma.infratalk.domain.board.usecase.DeleteBoardContentLikeUseCase
import com.seungma.infratalk.presenter.board.form.BoardLikeDeleteForm
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.jupiter.api.Assertions.assertFalse
import java.util.Date

class DeleteBoardContentLikeUseCaseTest {
    private val repository: LikeDataRepository = mockk()
    private lateinit var useCase: DeleteBoardContentLikeUseCase

    @Before
    fun setUp() {
        useCase = DeleteBoardContentLikeUseCase(repository)
    }

    @Test
    fun `게시글 콘텐츠 좋아요 삭제 테스트`() = runTest {
        //given
        val currentTime = System.currentTimeMillis()
        val boardLikeDeleteForm: BoardLikeDeleteForm = mockk(relaxed = true) {
            every { boardAuthorEmail } returns "email"
            every { boardCreateTime } returns Date(currentTime)
        }
        val boardEntity: BoardEntity = mockk(relaxed = true) {
            every { likeEntity.isLike } returns true
            every { boardMetaEntity } returns mockk(relaxed = true) {
                every { author.email } returns "email"
                every { createTime } returns Date(currentTime)
            }
        }

        coEvery { repository.deleteBoardLike(any()) } returns LikeEntity(isLike = false)
        coEvery { repository.loadBoardLikeCount(any())} returns mockk(relaxed = true)

        //when
        val result = useCase.invoke(boardLikeDeleteForm, mockk(), boardEntity)

        //then
        assertFalse(result.likeEntity.isLike)

        coVerify(exactly = 1) {repository.deleteBoardLike(any())}
        coVerify(exactly = 1) {repository.loadBoardLikeCount(any())}
    }
}