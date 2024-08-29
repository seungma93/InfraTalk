package com.seungma.infratalk.domain.mypage.usecase

import android.net.Uri
import android.util.Log
import com.seungma.infratalk.data.model.request.image.ImagesRequest
import com.seungma.infratalk.domain.image.usecase.UploadImagesUseCase
import com.seungma.infratalk.domain.user.repository.UserDataRepository
import com.seungma.infratalk.domain.user.entity.UserEntity
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
                Log.d("seungma", "업데이트유저인포")
                userDataRepository.updateUserInfo(userInfoUpdateForm.copy(image = uploadImageResult.successUris[0]))
            }
        }
    }

}