package com.example.frontend.api.models

data class RegisterResponse(
    val reuslt:Int,
    val content:TokenContent
)
data class TokenContent(
    val token:String
)