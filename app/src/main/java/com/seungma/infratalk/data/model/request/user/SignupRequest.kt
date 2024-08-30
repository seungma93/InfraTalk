package com.seungma.infratalk.data.model.request.user

data class SignupRequest(
    val email: String,
    val password: String,
    val nickname: String,
    val imageUri: String?
)