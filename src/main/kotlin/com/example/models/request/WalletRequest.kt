package com.example.models.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

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