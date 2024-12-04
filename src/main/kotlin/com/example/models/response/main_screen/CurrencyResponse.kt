package com.example.models.response.main_screen

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Сущность валюты для клиент-серверного взаимодействия (то что отдаем, response)
 * Аннотации SerialName говорят об имени элемента в json, то что после val - название элемента в приложении
 */
@Serializable
data class CurrencyResponse(
    @SerialName("currency_id")
    val currencyId: Long? = null,
    @SerialName("name")
    val name: String,
    @SerialName("value")
    val course: String? = null,
    @SerialName("full_name")
    val fullName: String? = null,
    @SerialName("full_list_name")
    val fullListName: String? = null,
    @SerialName("icon")
    val icon: String? = null,
    @SerialName("is_hide")
    val isHide: Boolean
)