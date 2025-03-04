package com.sm.infratalk.data.datasource.remote.user

import com.sm.infratalk.data.model.request.user.DeleteUserRequest
import com.sm.infratalk.data.model.request.user.LoginRequest
import com.sm.infratalk.data.model.request.user.ResetPasswordRequest
import com.sm.infratalk.data.model.request.user.SignupRequest
import com.sm.infratalk.data.model.request.user.UserInfoUpdateRequest
import com.sm.infratalk.data.model.request.user.UserSelectRequest
import com.sm.infratalk.data.model.response.user.UserResponse

interface UserDataSource {
    suspend fun signUp(signupRequest: SignupRequest): UserResponse
    suspend fun login(loginRequest: LoginRequest): UserResponse
    suspend fun resetPassword(resetPasswordRequest: ResetPasswordRequest): UserResponse
    suspend fun updateUserInfo(userInfoUpdateRequest: UserInfoUpdateRequest): UserResponse
    suspend fun sendVerifiedEmail(): UserResponse
    suspend fun deleteUserInfo(deleteUserRequest: DeleteUserRequest): UserResponse
    suspend fun selectUserInfo(userSelectRequest: UserSelectRequest): UserResponse
    suspend fun getUserMe(): UserResponse
    fun logout()
}