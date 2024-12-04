package com.example.models.response.main_screen

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Сущность списка валют для главной для клиент-серверного взаимодействия (то что отдаем, response)
 * Аннотации SerialName говорят об имени элемента в json, то что после val - название элемента в приложении
 */
@Serializable
data class ExchangeRatesResponse(
    @SerialName("first_currency")
    val firstCurrency: CurrencyResponse,
    @SerialName("second_currency")
    val secondCurrency: CurrencyResponse,
    @SerialName("third_currency")
    val thirdCurrency: CurrencyResponse
)