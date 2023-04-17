package com.freetalk.usecase

import com.freetalk.data.entity.UserEntity
import com.freetalk.data.remote.ImagesRequest
import com.freetalk.data.remote.NoImageException
import com.freetalk.data.remote.UpdateForm
import javax.inject.Inject

interface UpdateProfileImageUseCase {
    suspend fun updateProfileImage(imageRequest: ImagesRequest?, updateForm: UpdateForm): UserEntity
}

class UpdateProfileImageUseCaseImpl @Inject constructor(
    private val uploadImagesUseCase: UploadImagesUseCase,
    private val updateUserInfoUseCase: UpdateUserInfoUseCase
) : UpdateProfileImageUseCase {
    override suspend fun updateProfileImage(
        imageRequest: ImagesRequest?,
        updateForm: UpdateForm
    ): UserEntity {
        return when (imageRequest) {
            null -> UserEntity(updateForm.email, updateForm.nickname!!, updateForm.image)
            else -> {
                val uploadImageResult = uploadImagesUseCase.uploadImages(imageRequest)
                when (uploadImageResult.successUris.isEmpty()) {
                    true -> throw NoImageException("업로드할 이미지가 없습니다")
                    false -> { updateUserInfoUseCase.updateUserInfo(
                            UpdateForm(
                                updateForm.email,
                                updateForm.nickname,
                                uploadImageResult.successUris[0]
                            )
                        )
                    }
                }
            }
        }
    }
}

