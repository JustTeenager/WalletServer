package com.example.models.response

import com.example.models.response.main_screen.CurrencyResponse
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Сущность транзакций для клиент-серверного взаимодействия (то что отдаем, response)
 * Аннотации SerialName говорят об имени элемента в json, то что после val - название элемента в приложении
 */
@Serializable
data class TransactionResponse(
    @SerialName("transaction_id")
    val id: Long,
    @SerialName("value")
    val money: String,
    val category: CategoryResponse,
    val currency: CurrencyResponse,
    @SerialName("transaction_time")
    val time: Long,
    @SerialName("wallet_id")
    val walletId: Long? = null,
)