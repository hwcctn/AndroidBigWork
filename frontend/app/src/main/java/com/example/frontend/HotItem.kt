package com.example.frontend

data class HotItem(

val id: Int,
val date: Long,
val title: String,
val sender: String,
val content: List<String>,
val tags: List<String>,
val images: List<String>,
var isFollowing:Boolean,

)