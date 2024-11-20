package com.example.database

import com.example.utils.dbQuery
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import java.math.BigDecimal
import java.util.Calendar


data class ExposedTransaction(
    val transactionId: Long,
    val money: BigDecimal,
    val categoryId: Long,
    val currencyId: Long,
    val time: Long,
    val walletId: Long,
    val type: Int = -1,
    val operation: String = "",
    val iconId: Int = 0
)

class TransactionService(database: Database) {
    object Transaction : Table() {
        val id = long("id").autoIncrement()
        val money = decimal("value", 25, 5)
        val idCategory = long("category_id")
        val idCurrency = long("currency_id")
        val time = long("time")
        val walletId = long("wallet_id")

        override val primaryKey = PrimaryKey(id)
    }

    init {
        transaction(database) {
            SchemaUtils.create(Transaction)
        }
    }

    suspend fun create(walletId: Long, transaction: ExposedTransaction): ExposedTransaction? {
        return dbQuery {
            val id = Transaction.insert {
                it[money] = transaction.money
                it[idCategory] = transaction.categoryId
                it[idCurrency] = transaction.currencyId
                it[time] = transaction.time
                it[Transaction.walletId] = walletId
            }[Transaction.id]
            getTransaction(id)
        }
    }

    suspend fun getTransactionsViaWalletId(walletId: Long): List<ExposedTransaction> {
        return dbQuery {
            Join(
                Transaction, CategoryService.Category,
                onColumn = Transaction.idCategory, otherColumn = CategoryService.Category.id,
                joinType = JoinType.INNER
            ).select {
                Transaction.walletId eq walletId
            }.map {
                mapTransaction(it)
            }
        }
    }

    suspend fun getById(transactionId: Long): ExposedTransaction? {
        return dbQuery {
            getTransaction(transactionId)
        }
    }

    suspend fun updateTransaction(transactionId: Long, transaction: ExposedTransaction) {
        return dbQuery {
            Transaction.update({ Transaction.id eq transactionId }) {
                it[time] = transaction.time
                it[idCategory] = transaction.categoryId
                it[idCurrency] = transaction.currencyId
                it[money] = transaction.money
            }
        }
    }

    suspend fun deleteTransaction(transactionId: Long): Boolean {
        return dbQuery {
            val result = Transaction.deleteWhere { id eq transactionId }
            result > 0
        }
    }

    suspend fun getTransactionByTime(walletId: Long, dates: List<Calendar>): List<List<ExposedTransaction>>? {
        return dbQuery {
            if (dates.size != 5) return@dbQuery null
            buildList {
                add(
                    getJoinTransaction {
                        Transaction.walletId eq walletId and (Transaction.time greaterEq dates[0].timeInMillis) and
                                (Transaction.time lessEq dates[1].timeInMillis)
                    }
                )
                add(
                    getJoinTransaction {
                        Transaction.walletId eq walletId and (Transaction.time greaterEq dates[1].timeInMillis) and
                                (Transaction.time lessEq dates[2].timeInMillis)
                    }
                )
                add(
                    getJoinTransaction {
                        Transaction.walletId eq walletId and (Transaction.time greaterEq dates[2].timeInMillis) and
                                (Transaction.time lessEq dates[3].timeInMillis)
                    }
                )
                add(
                    getJoinTransaction {
                        Transaction.walletId eq walletId and (Transaction.time greaterEq dates[3].timeInMillis) and
                                (Transaction.time lessEq dates[4].timeInMillis)
                    }
                )
                add(getJoinTransaction {
                    Transaction.walletId eq walletId and (Transaction.time greaterEq dates[4].timeInMillis)
                })
            }
        }
    }

    private fun getJoinTransaction(select: SqlExpressionBuilder.() -> Op<Boolean>) = Join(
        Transaction, CategoryService.Category,
        onColumn = Transaction.idCategory, otherColumn = CategoryService.Category.id,
        joinType = JoinType.INNER
    ).select(select).map {
        mapTransaction(it)
    }

    private fun getTransaction(transactionId: Long) = Join(
        Transaction, CategoryService.Category,
        onColumn = Transaction.idCategory, otherColumn = CategoryService.Category.id,
        joinType = JoinType.INNER
    ).select {
        Transaction.id eq transactionId
    }.map {
        mapTransaction(it)
    }.singleOrNull()

    private fun mapTransaction(it: ResultRow) = ExposedTransaction(
        transactionId = it[Transaction.id],
        money = it[Transaction.money],
        categoryId = it[Transaction.idCategory],
        currencyId = it[Transaction.idCurrency],
        time = it[Transaction.time],
        walletId = it[Transaction.walletId],
        type = it[CategoryService.Category.type],
        operation = it[CategoryService.Category.operation],
        iconId = it[CategoryService.Category.idIcon]
    )
}