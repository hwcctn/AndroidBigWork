package com.example.frontend.api.models

data class LoginResponse(
    val message: String,
    val content: Content
)
data class Content(
    val token: String
)