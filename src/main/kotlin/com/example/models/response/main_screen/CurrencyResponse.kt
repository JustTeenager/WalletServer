package com.example.models.response.main_screen

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

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
    val icon: String? = null,
    @SerialName("is_up")
    val isUp: Boolean
)