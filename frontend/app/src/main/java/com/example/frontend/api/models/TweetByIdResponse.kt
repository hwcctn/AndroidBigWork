package com.example.frontend.api.models

data class TweetByIdResponse(
    val reuslt: Int,
    val content: Content
)

data class Content(
    val date: Long,
    val title: String,
    val sender: String,
    val content: List<String>,
    val tags: List<String>,
    val images: List<String>
)