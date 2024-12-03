package com.example.models.response.cart_info

import com.example.models.response.CategoryResponse
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CategoriesByMonthResponse(
    @SerialName("category")
    val category: CategoryResponse,
    @SerialName("sum")
    val sum: String,
)