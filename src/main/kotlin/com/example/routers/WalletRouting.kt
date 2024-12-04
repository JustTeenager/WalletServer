package com.example.routers

import com.example.database.CourceService
import com.example.database.ExposedCource
import com.example.database.ExposedWallet
import com.example.database.WalletService
import com.example.mappers.toCurrencyResponse
import com.example.models.request.WalletRequest
import com.example.models.response.WalletResponse
import com.example.utils.toStringWithFormat
import com.example.utils.withUserId
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.Database
import java.math.BigDecimal
import kotlin.random.Random

/**
 * Создание ручек работы с кошельками
 */
fun Application.walletRouting(database: Database) {
    val walletService = WalletService(database)
    val courceService = CourceService(database)
    routing {
        authenticate("auth-jwt") {
            /**
             * Ручка получения списка кошельков
             */
            get("/wallets") {
                withUserId { userId ->
                    val wallets = walletService.getWalletsByUser(userId)
                    call.respond(wallets.map { it.toWalletResponse() })
                }
            }

            /**
             * Ручка удаления кошелька по айди
             * Перед овтетом проверяем, получилось ли удалить кошелек
             */
            delete("/wallets/{walletId}") {
                withUserId { userId ->
                    val walletId = call.parameters["walletId"]?.toLong() ?: -1
                    val isDelete = walletService.deleteWallet(walletId, userId)
                    if (isDelete) {
                        val response = getMainScreenResponse(walletService, userId, courceService)
                        call.respond(response)
                        return@withUserId
                    }
                    call.respond(
                        HttpStatusCode.BadRequest, "Ошибка! Не удалось удалить кошелек"
                    )
                }
            }

            /**
             * Ручка создание кошелька
             */
            post("/wallets") {
                withUserId { userId ->
                    val walletRequest = call.receive<WalletRequest>()
                    val wallet = walletService.createWallet(walletRequest.toExposedWallet(), userId)
                    call.respond(wallet.toWalletResponse())
                }
            }

            /**
             * Ручка обновления кошелька
             * Проходит ряд проверок (на курс, на само редактирование, на возврат отредактированного значения)
             */
            put("/wallets/{walletId}") {
                withUserId { userId ->
                    val walletId = call.parameters["walletId"]?.toLong() ?: -1
                    val walletRequest = call.receive<WalletRequest>()
                    val exposed = walletRequest.toExposedWallet(walletId)
                    val cource = courceService.getCourceById(exposed.currency.currencyId)
                    if (cource == null) {
                        call.respond(HttpStatusCode.BadRequest, "Ошибка! Не удалось получить валюту кошелька!")
                        return@withUserId
                    }
                    val isSuccess = walletService.editWallet(
                        exposed,
                        userId,
                        cource
                    )
                    if (!isSuccess) {
                        call.respond(HttpStatusCode.BadRequest, "Ошибка! Не удалось отредактировать кошелек!")
                        return@withUserId
                    }
                    val wallet = walletService.getById(walletId, userId)
                    if (wallet == null) {
                        call.respond(
                            HttpStatusCode.BadRequest,
                            "Ошибка! Не удалось получить значения кошелька после его редактирования! " +
                                    "Сам кошелек уже отредактирован"
                        )
                        return@withUserId
                    }
                    call.respond(wallet.toWalletResponse())
                }
            }
        }
    }
}

private fun WalletRequest.toExposedWallet(walletId: Long = Random.nextLong()) = ExposedWallet(
    id = walletId,
    name = this.name,
    limit = this.limit?.let { BigDecimal(it) },
    isHide = this.isHide,
    income = BigDecimal("0"),
    outcome = BigDecimal("0"),
    currency = ExposedCource(
        currencyId = this.currencyId,
    )
)

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
