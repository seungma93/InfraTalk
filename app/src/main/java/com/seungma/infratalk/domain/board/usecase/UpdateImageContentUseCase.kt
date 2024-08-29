package com.seungma.infratalk.domain.board.usecase

import com.seungma.infratalk.data.model.request.image.ImagesRequest
import com.seungma.infratalk.domain.image.usecase.UploadImagesUseCase
import com.seungma.infratalk.presenter.board.form.BoardContentImagesUpdateForm
import com.seungma.infratalk.presenter.board.form.BoardUpdateForm
import javax.inject.Inject

class UpdateBoardContentImagesUseCase @Inject constructor(
    private val updateBoardContentUseCase: UpdateBoardContentUseCase,
    private val uploadImagesUseCase: UploadImagesUseCase
) {
    suspend operator fun invoke(boardContentImagesUpdateForm: BoardContentImagesUpdateForm) {
        uploadImagesUseCase.uploadImages(ImagesRequest(boardContentImagesUpdateForm.images))
        updateBoardContentUseCase(
            boardUpdateForm = BoardUpdateForm(
                authorEmail = boardContentImagesUpdateForm.boardAuthorEmail,
                createTime = boardContentImagesUpdateForm.boardCreateTime,
                title = null,
                content = null,
                images = boardContentImagesUpdateForm.images
            )
        )
    }
}