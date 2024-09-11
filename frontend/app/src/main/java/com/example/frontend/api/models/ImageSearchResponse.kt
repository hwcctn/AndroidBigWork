package com.example.frontend.api.models


data class ImageSearchResponse(
    val id: Long,
    val distance: Double,
    val entity: Entity
)

data class Entity(
    val filename: String,
    val subject: String
)