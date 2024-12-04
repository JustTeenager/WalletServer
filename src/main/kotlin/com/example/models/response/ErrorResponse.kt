package com.example.models.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Сущность ошибки для клиент-серверного взаимодействия (то что отдаем, response)
 * Аннотации SerialName говорят об имени элемента в json, то что после val - название элемента в приложении
 */
@Serializable
data class ErrorResponse(
    @SerialName("message")
    val message: String,
    @SerialName("code_description")
    val codeDescription: String
)