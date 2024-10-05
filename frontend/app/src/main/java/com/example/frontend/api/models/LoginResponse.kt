package com.example.frontend.api.models

data class LoginResponse(
    val message: String,
    val content: LoginContent
)
data class LoginContent(
    val token: String
)