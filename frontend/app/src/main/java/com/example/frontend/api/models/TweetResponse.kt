package com.example.frontend.api.models

data class TweetResponse(
    val reuslt: Int,
    val content: List<TweetItem>
)

data class TweetItem(
    val id: Int,
    val tweet: Tweet
)

data class Tweet(
    val id: Int = 1,
    val date: Long,
    val title: String,
    val sender: String,
    val content: List<String>,
    val tags: List<String>,
    val images: List<String>
)
