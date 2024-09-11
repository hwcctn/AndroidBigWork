package com.example.frontend.api.models

data class NewTweetRequest(
    val title: String,
    val content: List<String>,
    val tags: List<String>,
    val images: List<String>
)