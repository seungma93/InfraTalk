package com.seungma.infratalk.data.datasource.remote

import com.seungma.infratalk.data.model.request.SignupRequest
import com.seungma.infratalk.data.model.request.user.DeleteUserRequest
import com.seungma.infratalk.data.model.request.user.LoginRequest
import com.seungma.infratalk.data.model.request.user.ResetPasswordRequest
import com.seungma.infratalk.data.model.request.user.UserInfoUpdateRequest
import com.seungma.infratalk.data.model.request.user.UserSelectRequest
import com.seungma.infratalk.data.model.response.user.UserResponse
import com.seungma.infratalk.domain.user.UserEntity

interface UserDataSource {
    suspend fun signUp(signupRequest: SignupRequest): UserResponse
    suspend fun login(loginRequest: LoginRequest): UserResponse
    suspend fun resetPassword(resetPasswordRequest: ResetPasswordRequest): UserResponse
    suspend fun updateUserInfo(userInfoUpdateRequest: UserInfoUpdateRequest): UserResponse
    suspend fun sendVerifiedEmail(): UserResponse
    suspend fun deleteUserInfo(deleteUserRequest: DeleteUserRequest): UserResponse
    fun getUserInfo(): UserEntity
    suspend fun selectUserInfo(userSelectRequest: UserSelectRequest): UserResponse
    fun obtainUser(): UserResponse
}