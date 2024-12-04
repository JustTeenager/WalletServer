package com.example.models.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Сущность категорий для клиент-серверного взаимодействия (то что отдаем, response)
 * Аннотации SerialName говорят об имени элемента в json, то что после val - название элемента в приложении
 */
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