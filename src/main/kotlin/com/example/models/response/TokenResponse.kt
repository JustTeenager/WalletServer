package com.example.models.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Сущность токена для клиент-серверного взаимодействия (то что отдаем, response)
 * Аннотации SerialName говорят об имени элемента в json, то что после val - название элемента в приложении
 */
@Serializable
class TokenResponse(
    @SerialName("token")
    val token: String
)