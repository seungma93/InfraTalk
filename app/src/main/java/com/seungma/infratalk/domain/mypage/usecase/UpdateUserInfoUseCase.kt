package com.seungma.infratalk.domain.mypage.usecase

import android.net.Uri
import com.seungma.infratalk.data.model.request.image.ImagesRequest
import com.seungma.infratalk.domain.image.UploadImagesUseCase
import com.seungma.infratalk.domain.user.UserDataRepository
import com.seungma.infratalk.domain.user.UserEntity
import com.seungma.infratalk.presenter.mypage.fragment.MyAccountInfoEditFragment
import com.seungma.infratalk.presenter.sign.form.UserInfoUpdateForm
import javax.inject.Inject

class UpdateUserInfoUseCase @Inject constructor(
    private val userDataRepository: UserDataRepository,
    private val uploadImagesUseCase: UploadImagesUseCase
) {
    suspend operator fun invoke(userInfoUpdateForm: UserInfoUpdateForm): UserEntity {
        val imageUri = userInfoUpdateForm.image

        return when (imageUri == null || imageUri == Uri.parse(MyAccountInfoEditFragment.DEFAULT_PROFILE_IMAGE)) {
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