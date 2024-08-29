package com.seungma.infratalk.domain.usecase

import com.seungma.infratalk.domain.board.repository.LikeDataRepository
import com.seungma.infratalk.domain.board.entity.LikeEntity
import com.seungma.infratalk.domain.board.entity.BoardListEntity
import com.seungma.infratalk.domain.board.usecase.AddBoardLikeUseCase
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

class AddBoardLikeUseCaseTest {
    private val repository: LikeDataRepository = mockk()
    private lateinit var useCase: AddBoardLikeUseCase

    @Before
    fun setUp() {
        useCase = AddBoardLikeUseCase(repository)
    }

    @Test
    fun `게시글 좋아요 추가 테스트`() = runTest {
        //given
        val currentTime = System.currentTimeMillis()
        val boardLikeAddForm: BoardLikeAddForm = mockk(relaxed = true) {
            every { boardAuthorEmail } returns "email"
            every { boardCreateTime } returns Date(currentTime)
        }
        val entity = BoardListEntity(
            boardList = listOf(
                mockk(relaxed = true) {
                    every { likeEntity.isLike } returns false
                    every { boardMetaEntity } returns mockk(relaxed = true) {
                        every { author.email } returns "email"
                        every { createTime } returns Date(currentTime)
                    }
                }
            )
        )

        coEvery { repository.addBoardLike(any()) } returns LikeEntity(isLike = true)
        coEvery { repository.loadBoardLikeCount(any()) } returns mockk()

        //when
        val result = useCase.invoke(boardLikeAddForm, mockk(), entity)

        //then
        assertTrue(result.boardList[0].likeEntity.isLike)

        coVerify(exactly = 1) { repository.addBoardLike(any()) }
        coVerify(exactly = 1) { repository.loadBoardLikeCount(any()) }
    }
}