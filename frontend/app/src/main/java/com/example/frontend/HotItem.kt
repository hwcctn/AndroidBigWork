package com.example.frontend

data class HotItem(
    val name: String,
    val title: String,
    val content: String,
    val images: List<String>  // 假设图片是存储在一个 URL 列表中
)