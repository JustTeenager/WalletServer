package com.example.routers

import com.example.database.CourceService
import com.example.database.ExposedWallet
import com.example.database.WalletService
import com.example.mappers.toCurrencyResponse
import com.example.mappers.toWalletResponse
import com.example.models.response.main_screen.BalanceResponse
import com.example.models.response.main_screen.MainScreenResponse
import com.example.utils.toStringWithFormat
import com.example.utils.withUserId
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.Database

fun Application.mainScreenRouting(database: Database) {
    val walletService = WalletService(database)
    val courceService = CourceService(database)

    routing {
        authenticate("auth-jwt") {
            get("/wallets/person/all") {
                withUserId { userId ->
                    val response = getMainScreenResponse(walletService, userId, courceService)
                    call.respond(response)
                }
            }
        }
    }
}

private suspend fun getMainScreenResponse(
    walletService: WalletService,
    userId: Long,
    courceService: CourceService
): MainScreenResponse {
    val exposedWallets = walletService.getWalletsByUser(userId)

    val balance = getBalanceRequest(exposedWallets)
    val exchanges = courceService.getCoursesWithoutRub().map { it.toCurrencyResponse() }
    val wallets = exposedWallets.map { it.toWalletResponse() }
    return MainScreenResponse(balance, exchanges, wallets)
}

private fun getBalanceRequest(wallets: List<ExposedWallet>): BalanceResponse {
    val incomeSum = wallets.sumOf { it.income * it.currency.course }
    val outcomeSum = wallets.sumOf { it.outcome * it.currency.course }
    return BalanceResponse(
        incomeMoney = incomeSum.toStringWithFormat(),
        consumptionMoney = outcomeSum.toStringWithFormat(),
        amountMoney = (incomeSum - outcomeSum).toStringWithFormat()
    )
}