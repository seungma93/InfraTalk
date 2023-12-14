package com.freetalk.domain.usecase

import com.freetalk.data.NoImageException
import com.freetalk.data.model.request.ImagesRequest
import com.freetalk.domain.entity.UserEntity
import com.freetalk.presenter.form.UserInfoUpdateForm
import javax.inject.Inject

class UpdateProfileImageUseCase @Inject constructor(
    private val uploadImagesUseCase: UploadImagesUseCase,
    private val updateUserInfoUseCase: UpdateUserInfoUseCase
) {
    suspend operator fun invoke(
        imageRequest: ImagesRequest?,
        userInfoUpdateForm: UserInfoUpdateForm
    ): UserEntity {
        return when (imageRequest) {
            null -> UserEntity(userInfoUpdateForm.email, userInfoUpdateForm.nickname!!, userInfoUpdateForm.image)
            else -> {
                val uploadImageResult = uploadImagesUseCase.uploadImages(imageRequest)
                when (uploadImageResult.successUris.isEmpty()) {
                    true -> throw NoImageException("업로드할 이미지가 없습니다")
                    false -> { updateUserInfoUseCase(
                        UserInfoUpdateForm(
                                userInfoUpdateForm.email,
                                userInfoUpdateForm.nickname,
                                uploadImageResult.successUris[0]
                            )
                        )
                    }
                }
            }
        }
    }
}

