package com.freetalk.domain.usecase

import com.freetalk.data.model.request.ImagesRequest
import com.freetalk.domain.entity.BoardEntity
import com.freetalk.domain.entity.BoardMetaEntity
import com.freetalk.presenter.form.BoardUpdateForm
import javax.inject.Inject

interface UpdateImageContentUseCase {
    suspend fun updateImageContent(boardUpdateForm: BoardUpdateForm): BoardMetaEntity
}

class UpdateImageContentUseCaseImpl @Inject constructor(
    private val updateContentUseCase: UpdateBoardContentUseCase,
    private val uploadImagesUseCase: UploadImagesUseCase
): UpdateImageContentUseCase{
    override suspend fun updateImageContent(boardUpdateForm: BoardUpdateForm): BoardMetaEntity {

        return when(boardUpdateForm.images){
            null -> updateContentUseCase.invoke(boardUpdateForm)
            else -> {
                val uploadImagesResult = uploadImagesUseCase.uploadImages(ImagesRequest(boardUpdateForm.images))

                val boardImageUpdateForm = BoardUpdateForm(
                    boardUpdateForm.author,
                    boardUpdateForm.title,
                    boardUpdateForm.content,
                    uploadImagesResult.successUris,
                    boardUpdateForm.createTime
                )
                val updateContentResult = updateContentUseCase.invoke(boardImageUpdateForm)

                BoardMetaEntity(updateContentResult.author,
                    updateContentResult.title,
                    updateContentResult.content,
                    uploadImagesResult,
                    updateContentResult.createTime,
                    updateContentResult.editTime
                )
            }
        }
    }
}