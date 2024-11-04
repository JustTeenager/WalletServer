package com.example.models.response

import com.example.models.response.main_screen.CurrencyResponse
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WalletResponse(
    @SerialName("wallet_id")
    val id: Long,
    val name: String,
    @SerialName("amount_money")
    val amountMoney: String,
    val income: String,
    val consumption: String,
    val limit: String?,
    val currency: CurrencyResponse,
    @SerialName("is_hide")
    val isHide: Boolean
)