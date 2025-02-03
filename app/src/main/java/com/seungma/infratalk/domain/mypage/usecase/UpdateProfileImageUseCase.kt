package com.seungma.infratalk.domain.mypage.usecase

import android.util.Log
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
            when (imageRequest) {
                null -> UserEntity(
                    userInfoUpdateForm.email,
                    userInfoUpdateForm.nickname!!,
                    userInfoUpdateForm.image
                )

                else -> {
                    Log.d("seungma","UpdateProfileImageUseCase.invoke")
                    val uploadImageResult = uploadImagesUseCase.uploadImages(imageRequest)
                    when (uploadImageResult.successUris.isEmpty()) {
                        true -> throw com.seungma.infratalk.data.NoImageException("업로드할 이미지가 없습니다")
                        false -> {
                            updateUserInfoUseCase(
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
        }.onFailure {
            throw FailUpdateProfileImageException(_message = "프로필 사진 업데이트 실패", throwable = it)
        }.getOrThrow()










    }
}

