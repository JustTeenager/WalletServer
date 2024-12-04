package com.example.models.response.cart_info

import com.example.models.response.cart_info.ChartDataResponse
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Сущность аналитики для клиент-серверного взаимодействия (то что отдаем, response)
 * Аннотации SerialName говорят об имени элемента в json, то что после val - название элемента в приложении
 */
@Serializable
data class ChartInfoResponse(
    @SerialName("info")
    val info: List<ChartDataResponse>,
    @SerialName("currency_icon")
    val currencyIcon: String,
)