package com.example.models.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreateTransactionRequest(
    @SerialName("wallet_id")
    val idWallet: Long,
    @SerialName("value")
    val money: String,
    @SerialName("category_id")
    val idCategory: Long,
    @SerialName("currency_id")
    val currencyId: Long,
    @SerialName("transaction_time")
    val time: Long
)