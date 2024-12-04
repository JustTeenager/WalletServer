package com.example.models.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Сущность кошелька для клиент-серверного взаимодействия (то что получаем, request)
 * Аннотации SerialName говорят об имени элемента в json, то что после val - название элемента в приложении
 */
@Serializable
data class WalletRequest(
    val name: String,
    @SerialName("limit")
    val limit: String? = null,
    @SerialName("currency_id")
    val currencyId: Long,
    @SerialName("is_hide")
    val isHide: Boolean
)