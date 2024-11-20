package com.example.models.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CategoryRequest(
    @SerialName("category")
    val type: Boolean,
    @SerialName("category_name")
    val operation: String,
    @SerialName("icon")
    val idIcon: Int
)