package com.seungma.infratalk.domain.usecase

import com.seungma.infratalk.domain.board.repository.LikeDataRepository
import com.seungma.infratalk.domain.board.entity.LikeEntity
import com.seungma.infratalk.domain.board.entity.BoardEntity
import com.seungma.infratalk.domain.board.usecase.AddBoardContentLikeUseCase
import com.seungma.infratalk.presenter.board.form.BoardLikeAddForm
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.jupiter.api.Assertions.assertTrue
import java.util.Date

class AddBoardContentLikeUseCaseTest {
    private val repository: LikeDataRepository = mockk()
    private lateinit var useCase: AddBoardContentLikeUseCase

    @Before
    fun setUp() {
        useCase = AddBoardContentLikeUseCase(repository)
    }

    @Test
    fun `게시글 콘텐츠 좋아요 추가 테스트`() = runTest {
        //given
        val currentTime = System.currentTimeMillis()
        val boardLikeAddForm: BoardLikeAddForm = mockk(relaxed = true) {
            every { boardAuthorEmail } returns "email"
            every { boardCreateTime } returns Date(currentTime)
        }
        val boardEntity: BoardEntity = mockk(relaxed = true) {
            every { likeEntity.isLike } returns false
            every { boardMetaEntity } returns mockk(relaxed = true) {
                every { author.email } returns "email"
                every { createTime } returns Date(currentTime)
            }
        }

        coEvery { repository.addBoardLike(any()) } returns LikeEntity(isLike = true)
        coEvery { repository.loadBoardLikeCount(any()) } returns mockk()

        //when
        val result = useCase.invoke(boardLikeAddForm, mockk(), boardEntity)

        //then
        assertTrue(result.likeEntity.isLike)

        coVerify(exactly = 1) { repository.addBoardLike(any()) }
        coVerify(exactly = 1) { repository.loadBoardLikeCount(any()) }
    }
}