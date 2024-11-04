package com.example.utils

import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import java.math.BigDecimal
import java.math.RoundingMode

fun connectDatabase(): Database {
    return Database.connect(
        url = "",
        user = "",
        driver = "com.mysql.cj.jdbc.Driver",
        password = "",
    )
}

suspend fun <T> dbQuery(block: suspend () -> T): T = newSuspendedTransaction(Dispatchers.IO) { block() }

fun BigDecimal.toStringWithFormat(): String {
    return this.setScale(2, RoundingMode.HALF_UP).toPlainString()
}