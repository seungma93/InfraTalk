package com.freetalk.domain.usecase

import com.freetalk.data.model.request.ImagesRequest
import com.freetalk.domain.entity.UserEntity
import com.freetalk.domain.repository.UserDataRepository
import com.freetalk.presenter.form.UserInfoUpdateForm
import javax.inject.Inject

class UpdateUserInfoUseCase @Inject constructor(
    private val userDataRepository: UserDataRepository,
    private val uploadImagesUseCase: UploadImagesUseCase
) {
    suspend operator fun invoke(userInfoUpdateForm: UserInfoUpdateForm): UserEntity {
        val imageUri = userInfoUpdateForm.image

        return when (imageUri == null) {
            true -> {
                userDataRepository.updateUserInfo(userInfoUpdateForm)
            }

            false -> {
                val uploadImageResult = uploadImagesUseCase.uploadImages(
                    imagesRequest = ImagesRequest(
                        imageUris = listOf(imageUri)
                    )
                )
                userDataRepository.updateUserInfo(userInfoUpdateForm.copy(image = uploadImageResult.successUris[0]))
            }
        }
    }

}