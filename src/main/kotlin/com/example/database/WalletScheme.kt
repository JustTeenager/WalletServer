package com.example.database

import com.example.utils.dbQuery
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.math.BigDecimal

data class ExposedWallet(
    val id: Long,
    val name: String,
    val isHide: Boolean,
    val income: BigDecimal,
    val outcome: BigDecimal,
    val limit: BigDecimal?,
    val currency: ExposedCource
)

class WalletService(database: Database) {
    object Wallets : Table() {
        val id = long("id").autoIncrement()
        val name = varchar("name", length = 50)
        val income = decimal("income_money", 25, 5)
        val outcome = decimal("outcome_money", 25, 5)
        val isHide = bool("is_hide")
        val currencyId = long("currency_id")
        val userId = long("user_id")
        val limit = decimal("limit", 25, 5).nullable()

        override val primaryKey = PrimaryKey(id)
    }

    init {
        transaction(database) {
            SchemaUtils.create(Wallets)
        }
    }

    suspend fun getWalletsByUser(userId: Long): List<ExposedWallet> {
        return dbQuery {
            Join(
                Wallets, CourceService.Cources,
                onColumn = Wallets.currencyId, otherColumn = CourceService.Cources.id,
                joinType = JoinType.INNER
            ).select {
                Wallets.userId eq userId
            }.map {
                ExposedWallet(
                    id = it[Wallets.id],
                    name = it[Wallets.name],
                    isHide = it[Wallets.isHide],
                    income = it[Wallets.income],
                    outcome = it[Wallets.outcome],
                    limit = it[Wallets.limit],
                    currency = ExposedCource(
                        it[Wallets.currencyId],
                        name = it[CourceService.Cources.name],
                        course = it[CourceService.Cources.cource],
                        fullName = it[CourceService.Cources.fullName],
                        fullListName = it[CourceService.Cources.fullListName],
                        icon = it[CourceService.Cources.icon],
                        isUp = it[CourceService.Cources.isUp]
                    )
                )
            }
        }
    }
}