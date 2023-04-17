package com.freetalk.usecase

import com.freetalk.data.entity.BoardEntity
import com.freetalk.data.entity.toEntity
import com.freetalk.data.remote.BoardResponse
import com.freetalk.data.remote.BoardUpdateForm
import com.freetalk.data.remote.ImagesRequest
import com.freetalk.repository.BoardDataRepository
import com.freetalk.repository.ImageDataRepository
import javax.inject.Inject

interface UpdateImageContentUseCase {
    suspend fun updateImageContent(boardUpdateForm: BoardUpdateForm): BoardEntity
}

class UpdateImageContentUseCaseImpl @Inject constructor(
    private val updateContentUseCase: UpdateContentUseCase,
    private val uploadImagesUseCase: UploadImagesUseCase
): UpdateImageContentUseCase{
    override suspend fun updateImageContent(boardUpdateForm: BoardUpdateForm): BoardEntity {

        return when(boardUpdateForm.images.isEmpty()){
            true -> updateContentUseCase.updateContent(boardUpdateForm)
            false -> {
                val uploadImagesResult = uploadImagesUseCase.uploadImages(ImagesRequest(boardUpdateForm.images))

                val boardImageUpdateForm = BoardUpdateForm(
                    boardUpdateForm.author,
                    boardUpdateForm.title,
                    boardUpdateForm.content,
                    uploadImagesResult.successUris,
                    boardUpdateForm.createTime,
                    boardUpdateForm.editTime
                )
                val updateContentResult = updateContentUseCase.updateContent(boardImageUpdateForm)

                BoardEntity(updateContentResult.author,
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