package com.freetalk.domain.usecase

import com.freetalk.data.model.request.ImagesRequest
import com.freetalk.domain.entity.BoardEntity
import com.freetalk.domain.entity.BoardMetaEntity
import com.freetalk.presenter.form.BoardContentImagesUpdateForm
import com.freetalk.presenter.form.BoardUpdateForm
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