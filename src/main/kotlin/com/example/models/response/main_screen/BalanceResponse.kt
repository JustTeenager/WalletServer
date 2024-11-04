package com.example.models.response.main_screen

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BalanceResponse(
    @SerialName("amount_money")
    val amountMoney: String,
    @SerialName("income_money")
    val incomeMoney: String,
    @SerialName("consumption_money")
    val consumptionMoney: String
)