package com.seungma.infratalk.domain.mypage.usecase

import android.util.Log
import com.seungma.infratalk.data.FailFirebaseUploadImageException
import com.seungma.infratalk.data.FailUpdateProfileImageException
import com.seungma.infratalk.data.model.request.image.ImagesRequest
import com.seungma.infratalk.domain.image.usecase.UploadImagesUseCase
import com.seungma.infratalk.domain.user.entity.UserEntity
import com.seungma.infratalk.presenter.sign.form.UserInfoUpdateForm
import javax.inject.Inject

class UpdateProfileImageUseCase @Inject constructor(
    private val uploadImagesUseCase: UploadImagesUseCase,
    private val updateUserInfoUseCase: UpdateUserInfoUseCase
) {
    suspend operator fun invoke(
        imageRequest: ImagesRequest?,
        userInfoUpdateForm: UserInfoUpdateForm
    ): UserEntity {
        return runCatching {
            imageRequest?.let {
                val uploadImageResult = uploadImagesUseCase.uploadImages(imageRequest)
                when (uploadImageResult.successUris.isEmpty()) {
                    true -> throw FailFirebaseUploadImageException(_message = "이미지 업로드 실패")
                    false -> {
                        updateUserInfoUseCase(
                            UserInfoUpdateForm(
                                userInfoUpdateForm.email,
                                userInfoUpdateForm.nickname,
                                uploadImageResult.successUris.first()
                            )
                        )
                    }
                }
            } ?: run {
                UserEntity(
                    userInfoUpdateForm.email,
                    userInfoUpdateForm.nickname!!,
                    userInfoUpdateForm.image
                )
            }
        }.onFailure {
            throw FailUpdateProfileImageException(_message = "프로필 사진 업데이트 실패", throwable = it)
        }.getOrThrow()










    }
}

