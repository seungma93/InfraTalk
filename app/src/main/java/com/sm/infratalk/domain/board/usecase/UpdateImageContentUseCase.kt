package com.sm.infratalk.domain.board.usecase

import com.sm.infratalk.data.model.request.image.ImagesRequest
import com.sm.infratalk.domain.image.usecase.UploadImagesUseCase
import com.sm.infratalk.presenter.board.form.BoardContentImagesUpdateForm
import com.sm.infratalk.presenter.board.form.BoardUpdateForm
import javax.inject.Inject

class UpdateBoardContentImagesUseCase @Inject constructor(
    private val updateBoardContentUseCase: UpdateBoardContentUseCase,
    private val uploadImagesUseCase: UploadImagesUseCase
) {
    suspend operator fun invoke(boardContentImagesUpdateForm: BoardContentImagesUpdateForm) {
        val imageResultEntity = uploadImagesUseCase.uploadImages(ImagesRequest(boardContentImagesUpdateForm.images))
        updateBoardContentUseCase(
            boardUpdateForm = BoardUpdateForm(
                authorEmail = boardContentImagesUpdateForm.boardAuthorEmail,
                createTime = boardContentImagesUpdateForm.boardCreateTime,
                title = null,
                content = null,
                images = imageResultEntity.successUris,
                editTime = boardContentImagesUpdateForm.editTime
            )
        )
    }
}