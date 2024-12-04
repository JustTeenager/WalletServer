package com.example.models.response.cart_info

import com.example.models.response.CategoryResponse
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Сущность категорий за месяц для клиент-серверного взаимодействия (то что отдаем, response)
 * Аннотации SerialName говорят об имени элемента в json, то что после val - название элемента в приложении
 */
@Serializable
data class CategoriesByMonthResponse(
    @SerialName("category")
    val category: CategoryResponse,
    @SerialName("sum")
    val sum: String,
)