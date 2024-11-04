package com.example.mappers

import com.example.database.ExposedCource
import com.example.database.ExposedWallet
import com.example.models.response.WalletResponse
import com.example.models.response.main_screen.CurrencyResponse
import com.example.utils.toStringWithFormat

fun ExposedWallet.toWalletResponse() = WalletResponse(
    id = this.id,
    name = this.name,
    amountMoney = this.income.minus(this.outcome).toStringWithFormat(),
    income = this.income.toStringWithFormat(),
    consumption = this.outcome.toStringWithFormat(),
    limit = this.limit?.toStringWithFormat(),
    currency = this.currency.toCurrencyResponse(),
    isHide = this.isHide
)

fun ExposedCource.toCurrencyResponse() = CurrencyResponse(
    currencyId = currencyId,
    name = name,
    course = course.toStringWithFormat(),
    fullName = fullName,
    fullListName = fullListName,
    icon = icon,
    isUp = isUp
)