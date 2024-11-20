package com.example.routers

import com.example.database.CourceService
import com.example.database.WalletService
import com.example.models.request.CreateTransactionRequest
import com.example.models.response.CategoryResponse
import com.example.models.response.TransactionResponse
import com.example.utils.toStringWithFormat
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.Database
import com.example.database.ExposedTransaction
import com.example.database.TransactionService
import com.example.models.response.main_screen.CurrencyResponse
import com.example.utils.toBoolean
import java.math.BigDecimal
import kotlin.random.Random

fun Application.transactionRouting(database: Database) {
    routing {
        authenticate("auth-jwt") {
            val transactionService = TransactionService(database)
            val walletService = WalletService(database)
            val courceService = CourceService(database)

            get("/transactions/{walletId}") {

                val walletId = call.parameters["walletId"]?.toLong() ?: -1
                val transactionList = transactionService.getTransactionsViaWalletId(walletId)

                call.respond(
                    transactionList.map {
                        val cource = courceService.getCourceById(it.currencyId)?.toCurrencyResponse()!!
                        it.toTransactionResponse(cource)
                    }
                )
            }

            post("/transactions") {
                val principal = call.authentication.principal<JWTPrincipal>()
                val userId = principal?.payload?.getClaim("user_id")?.asLong()!!
                val request = call.receive<CreateTransactionRequest>()
                val transaction = transactionService.create(request.idWallet, request.toExposedTransaction())!!
                val cource = courceService.getCourceById(transaction.currencyId)!!
                val courceResponse = cource.toCurrencyResponse()
                val isSuccess = walletService.increaseMoney(
                    request.idWallet,
                    if (transaction.type == 0) transaction.money else null,
                    if (transaction.type == 1) transaction.money else null,
                    cource,
                    userId
                )
                if (!isSuccess) {
                    call.respond(HttpStatusCode.BadRequest, "Ошибка! Не удалось создать транзакцию")
                    return@post
                }
                call.respond(
                    transaction.toTransactionResponse(courceResponse)
                )
            }

            put("/transactions/{transactionId}") {
                val principal = call.authentication.principal<JWTPrincipal>()
                val userId = principal?.payload?.getClaim("user_id")?.asLong()!!
                val transactionId = call.parameters["transactionId"]?.toLong() ?: -1
                val request = call.receive<CreateTransactionRequest>()
                val transaction = transactionService.getById(transactionId)!!
                val cource = courceService.getCourceById(transaction.currencyId)!!
                val courceResponse = cource.toCurrencyResponse()
                walletService.decreaseMoney(
                    transaction.walletId,
                    if (transaction.type == 0) transaction.money else null,
                    if (transaction.type == 1) transaction.money else null,
                    cource,
                    userId
                )
                transactionService.updateTransaction(transactionId, request.toExposedTransaction())
                val transactionAfterUpdate = transactionService.getById(transactionId)!!
                walletService.increaseMoney(
                    transactionAfterUpdate.walletId,
                    if (transactionAfterUpdate.type == 0) transactionAfterUpdate.money else null,
                    if (transactionAfterUpdate.type == 1) transactionAfterUpdate.money else null,
                    cource,
                    userId
                )
                call.respond(transactionAfterUpdate.toTransactionResponse(courceResponse))
            }

            delete("/transactions/{transactionId}") {
                val principal = call.authentication.principal<JWTPrincipal>()
                val userId = principal?.payload?.getClaim("user_id")?.asLong()!!
                val transactionId = call.parameters["transactionId"]?.toLong() ?: -1
                val transaction = transactionService.getById(transactionId)!!
                val cource = courceService.getCourceById(transaction.currencyId)!!
                transactionService.deleteTransaction(transactionId)
                val isSuccess = walletService.decreaseMoney(
                    transaction.walletId,
                    if (transaction.type == 0) transaction.money else null,
                    if (transaction.type == 1) transaction.money else null,
                    cource,
                    userId
                )
                if (!isSuccess) {
                    call.respond(HttpStatusCode.BadRequest, "Ошибка! Не удалось удалить транзакцию")
                    return@delete
                }
                val wallet = walletService.getById(transaction.walletId, userId)
                if (wallet == null) {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        "Ошибка! Не получить данные по кошельку после удаления транзакции"
                    )
                    return@delete
                }
                call.respond(wallet.toWalletResponse())
            }
        }
    }
}

fun CreateTransactionRequest.toExposedTransaction() = ExposedTransaction(
    transactionId = Random.nextLong(),
    money = BigDecimal(this.money),
    categoryId = this.idCategory,
    currencyId = this.currencyId,
    time = this.time,
    walletId = this.idWallet,
)

fun ExposedTransaction.toTransactionResponse(currencyResponse: CurrencyResponse) =
    TransactionResponse(
        id = this.transactionId,
        money = this.money.toStringWithFormat(),
        category = CategoryResponse(
            id = this.categoryId,
            type = this.type.toBoolean(),
            operation = this.operation,
            idIcon = this.iconId
        ),
        currency = currencyResponse,
        walletId = walletId,
        time = this.time,
    )