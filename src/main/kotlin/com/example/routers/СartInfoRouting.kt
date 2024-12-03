package com.example.routers

import com.example.database.*
import com.example.models.response.CategoryResponse
import com.example.models.response.ErrorResponse
import com.example.models.response.cart_info.CategoriesByMonthResponse
import com.example.models.response.cart_info.ChartDataResponse
import com.example.models.response.cart_info.ChartInfoResponse
import com.example.utils.toBoolean
import com.example.utils.toStringWithFormat
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.Database
import java.math.BigDecimal
import java.util.*

fun Application.cartInfoRouting(database: Database) {
    val transactionService = TransactionService(database)
    val courceService = CourceService(database)
    val walletService = WalletService(database)
    routing {
        authenticate("auth-jwt") {
            get("/chart-info/{walletId}") {
                val principal = call.authentication.principal<JWTPrincipal>()
                val userId = principal?.payload?.getClaim("user_id")?.asLong()!!
                val walletId = call.parameters["walletId"]?.toLong() ?: -1
                val wallet = walletService.getById(walletId, userId)
                if (wallet == null) {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        ErrorResponse(
                            message = "Ошибка! Не удалось получить данные по кошельку.",
                            codeDescription = "LoadWalletError"
                        )
                    )
                    return@get
                }
                val dates = getDates()
                val transactions = transactionService.getTransactionByTime(wallet.id, dates)
                val courcies = courceService.getCources()
                if (transactions == null || transactions.size != dates.size) {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        ErrorResponse(
                            message = "Ошибка! Не удалось загрузить транзакции за этот период времени.",
                            codeDescription = "LoadTransactionsError"
                        )
                    )
                    return@get
                }
                var maxValue = BigDecimal("0")
                val data = transactions.mapIndexed { index, exposedTransactions ->
                    val (chartData, value) =
                        sumOfTransactionByCategory(exposedTransactions, courcies, wallet.currency.course, dates[index])
                    if (maxValue < value) maxValue = value
                    chartData
                }
                if (maxValue == BigDecimal("0")) {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        ErrorResponse(
                            message = "Ошибка! Не удалось рассчитать деньги за этот период времени",
                            codeDescription = "CalculationError"
                        )
                    )
                    return@get
                }
                val finalData = data.map { chartData ->
                    ChartDataResponse(
                        incomeByMonth = chartData.incomeByMonth.toStringWithFormat(),
                        incomePercent = (chartData.incomeByMonth / maxValue).toFloat(),
                        outcomeByMonth = chartData.outcomeByMonth.toStringWithFormat(),
                        outcomePercent = (chartData.outcomeByMonth / maxValue).toFloat(),
                        categories = chartData.categories,
                        date = chartData.date,
                    )
                }
                call.respond(ChartInfoResponse(finalData, wallet.currency.icon))
            }
        }
    }
}

private fun getDates(): List<Calendar> {
    val calendar = Calendar.getInstance().apply {
        timeInMillis = System.currentTimeMillis()
    }
    return buildList {
        repeat(5) {
            add(Calendar.getInstance().apply {
                set(Calendar.YEAR, calendar.get(Calendar.YEAR))
                set(Calendar.MONTH, calendar.get(Calendar.MONTH))
                set(Calendar.DAY_OF_MONTH, 1)
                add(Calendar.MONTH, -it)
            })
        }
    }.reversed()
}

private fun sumOfTransactionByCategory(
    transactions: List<ExposedTransaction>,
    courcies: List<ExposedCource>,
    walletCource: BigDecimal,
    date: Calendar
): Pair<ChartData, BigDecimal> {
    var income = BigDecimal("0")
    var outCome = BigDecimal("0")
    val categories = mutableMapOf<Long, BigDecimal>()

    transactions.forEach { transaction ->
        val transactionCource = courcies.first { it.currencyId == transaction.currencyId }.course
        val cource = transactionCource / walletCource
        val money = transaction.money * cource
        if (transaction.type == 0) {
            income += money
        } else {
            outCome += money
        }
        val categoryMoney = categories[transaction.categoryId]
        categories[transaction.categoryId] = if (categoryMoney != null) categoryMoney + money else money
    }
    val maxValue = if (income > outCome) income else outCome
    return ChartData(
        incomeByMonth = income,
        outcomeByMonth = outCome,
        categories = categories.map { categoryData ->
            val category = transactions.first { it.categoryId == categoryData.key }
            CategoriesByMonthResponse(
                category = CategoryResponse(
                    id = category.categoryId,
                    type = category.type.toBoolean(),
                    operation = category.operation,
                    idIcon = category.iconId
                ),
                sum = categoryData.value.toStringWithFormat()
            )
        },
        date = date.timeInMillis
    ) to maxValue
}

private data class ChartData(
    val incomeByMonth: BigDecimal,
    val outcomeByMonth: BigDecimal,
    val categories: List<CategoriesByMonthResponse>,
    val date: Long,
)