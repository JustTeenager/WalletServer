package com.example.models.response.main_screen

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Сущность сведений о балансе для клиент-серверного взаимодействия (то что отдаем, response)
 * Аннотации SerialName говорят об имени элемента в json, то что после val - название элемента в приложении
 */
@Serializable
data class BalanceResponse(
    @SerialName("amount_money")
    val amountMoney: String,
    @SerialName("income_money")
    val incomeMoney: String,
    @SerialName("consumption_money")
    val consumptionMoney: String
)