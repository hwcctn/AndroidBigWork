package com.example.frontend


data class FanListUser
    (
    val username: String,
    val profilePictureBase64: String,
    var isFollowing: Boolean
)