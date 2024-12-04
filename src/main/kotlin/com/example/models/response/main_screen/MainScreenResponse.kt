package com.example.models.response.main_screen

import com.example.models.response.WalletResponse
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Сущность для отрисовки главного экрана на клиенте (то что отдаем, response)
 * Аннотации SerialName говорят об имени элемента в json, то что после val - название элемента в приложении
 */
@Serializable
data class MainScreenResponse(
    @SerialName("balance")
    val balance: BalanceResponse,
    @SerialName("currency_dto")
    val exchangeRatesResponse: List<CurrencyResponse>,
    @SerialName("wallets")
    val wallets: List<WalletResponse>
)