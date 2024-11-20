package com.example.models.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CategoryResponse(
    @SerialName("category_id")
    val id: Long,
    @SerialName("category")
    val type: Boolean,
    @SerialName("category_name")
    val operation: String,
    @SerialName("icon")
    val idIcon: Int
)