package com.sm.domain.repository

import android.util.Log
import com.sm.infratalk.data.datasource.local.preference.PreferenceDataSource
import com.sm.infratalk.data.datasource.remote.user.UserDataSource
import com.sm.infratalk.data.mapper.toEntity
import com.sm.infratalk.data.model.request.preference.SavedEmailSetRequest
import com.sm.infratalk.data.model.request.user.DeleteUserRequest
import com.sm.infratalk.data.model.request.user.LoginRequest
import com.sm.infratalk.data.model.request.user.ResetPasswordRequest
import com.sm.infratalk.data.model.request.user.SignupRequest
import com.sm.infratalk.data.model.request.user.UserInfoUpdateRequest
import com.sm.infratalk.domain.user.entity.SavedEmailGetEntity
import com.sm.infratalk.domain.user.entity.UserEntity
import com.sm.infratalk.domain.user.repository.UserDataRepository
import com.sm.infratalk.presenter.sign.form.LoginForm
import com.sm.infratalk.presenter.sign.form.ResetPasswordForm
import com.sm.infratalk.presenter.sign.form.SavedEmailSetForm
import com.sm.infratalk.presenter.sign.form.SignUpForm
import com.sm.infratalk.presenter.sign.form.UserInfoUpdateForm
import toEntity
import javax.inject.Inject

class UserDataRepositoryImpl @Inject constructor(private val dataSource: UserDataSource, private val preferenceDataSource: PreferenceDataSource) :
    UserDataRepository {

    override suspend fun signUp(signUpForm: SignUpForm): UserEntity = with(signUpForm) {
        Log.d("UserDataR", "시작")
        return dataSource.signUp(
            signupRequest = SignupRequest(
                email = email,
                password = password,
                nickname = nickname,
                imageUri = null
            )
        ).toEntity()
    }

    override suspend fun login(loginForm: LoginForm): UserEntity = with(loginForm) {
        Log.d("UserDataR", "로그인 레포")
        return dataSource.login(
            loginRequest = LoginRequest(
                email = email,
                password = password
            )
        ).toEntity()
    }

    override suspend fun resetPassword(resetPasswordForm: ResetPasswordForm): UserEntity {
        return dataSource.resetPassword(
            resetPasswordRequest = ResetPasswordRequest(
                resetPasswordForm.email
            )
        ).toEntity()
    }

    override suspend fun sendVerifiedEmail(): UserEntity {
        Log.d("SendEmail", "레포지토리")
        return dataSource.sendVerifiedEmail().toEntity()
    }

    override suspend fun updateUserInfo(userInfoUpdateForm: UserInfoUpdateForm): UserEntity =
        with(userInfoUpdateForm) {
            return dataSource.updateUserInfo(
                userInfoUpdateRequest = UserInfoUpdateRequest(
                    email = email,
                    nickname = nickname,
                    image = image
                )
            ).toEntity()
        }

    override suspend fun deleteUserInfo(signUpForm: SignUpForm): UserEntity {
        return dataSource.deleteUserInfo(
            deleteUserRequest = DeleteUserRequest(
                email = signUpForm.email
            )
        ).toEntity()
    }

    override suspend fun getUserMe(): UserEntity {
        return dataSource.getUserMe().toEntity()
    }

    override fun logout() {
        dataSource.logout()
    }

    override fun getSavedEmail(): SavedEmailGetEntity {
        return preferenceDataSource.getSavedEmail().toEntity()
    }

    override fun setSavedEmail(savedEmailSetForm: SavedEmailSetForm) {
        preferenceDataSource.setSavedEmail(
            savedEmailSetRequest = SavedEmailSetRequest(
                email = savedEmailSetForm.email
            )
        )
    }

    override fun deleteSavedEmail() {
        preferenceDataSource.deleteSavedEmail()
    }
}