package com.example.frontend.api.models

data class VerifyTokenResponse(
    val result: Int,
    val content: usernameContent,
)
data class usernameContent(
    val username: String
)