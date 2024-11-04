package com.example.models.response.main_screen

import com.example.models.response.WalletResponse
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MainScreenResponse(
    @SerialName("balance")
    val balance: BalanceResponse,
    @SerialName("currency_dto")
    val exchangeRatesResponse: List<CurrencyResponse>,
    @SerialName("wallets")
    val wallets: List<WalletResponse>
)