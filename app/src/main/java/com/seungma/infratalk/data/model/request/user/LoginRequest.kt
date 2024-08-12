package com.seungma.infratalk.data.model.request.user

data class LoginRequest(
    val email: String,
    val password: String
)