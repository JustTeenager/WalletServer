package com.example.models.response.main_screen

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ExchangeRatesResponse(
    @SerialName("first_currency")
    val firstCurrency: CurrencyResponse,
    @SerialName("second_currency")
    val secondCurrency: CurrencyResponse,
    @SerialName("third_currency")
    val thirdCurrency: CurrencyResponse
)