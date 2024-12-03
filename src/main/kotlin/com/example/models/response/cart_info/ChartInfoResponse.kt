package com.example.models.response.cart_info

import com.example.models.response.cart_info.ChartDataResponse
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ChartInfoResponse(
    @SerialName("info")
    val info: List<ChartDataResponse>,
    @SerialName("currency_icon")
    val currencyIcon: String,
)