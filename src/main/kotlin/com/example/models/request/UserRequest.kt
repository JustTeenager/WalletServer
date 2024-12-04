package com.example.models.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Сущность пользователя для клиент-серверного взаимодействия (то что получаем, request)
 * Аннотации SerialName говорят об имени элемента в json, то что после val - название элемента в приложении
 */
@Serializable
data class UserRequest(
    @SerialName("email")
    val email: String,
    @SerialName("name")
    val name: String,
)