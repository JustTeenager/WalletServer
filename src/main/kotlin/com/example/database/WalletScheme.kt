package com.example.database

import com.example.utils.dbQuery
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
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
                Wallets, CourceService.Courses,
                onColumn = Wallets.currencyId, otherColumn = CourceService.Courses.id,
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
                        name = it[CourceService.Courses.name],
                        course = it[CourceService.Courses.cource],
                        fullName = it[CourceService.Courses.fullName],
                        fullListName = it[CourceService.Courses.fullListName],
                        icon = it[CourceService.Courses.icon],
                        isUp = it[CourceService.Courses.isUp]
                    )
                )
            }
        }
    }


    suspend fun createWallet(wallet: ExposedWallet, usId: Long): ExposedWallet = dbQuery {
        val walletDB = Wallets.insert {
            it[name] = wallet.name
            it[limit] = wallet.limit
            it[isHide] = wallet.isHide
            it[currencyId] = wallet.currency.currencyId
            it[userId] = usId
            it[income] = wallet.income
            it[outcome] = wallet.outcome
        }
        wallet.copy(id = walletDB[Wallets.id])
    }

    suspend fun editWallet(
        wallet: ExposedWallet,
        userId: Long,
        newCource: ExposedCource
    ): Boolean = dbQuery {
        val currentWallet = getWallet(walletId = wallet.id, userId = userId) ?: return@dbQuery false
        Wallets.update({ Wallets.id eq wallet.id }) {
            if (currentWallet.currency.currencyId != wallet.currency.currencyId) {
                it[currencyId] = wallet.currency.currencyId
                val cource = currentWallet.currency.course / newCource.course
                it[income] = currentWallet.income * cource
                it[outcome] = currentWallet.outcome * cource
            }
            if (currentWallet.isHide != wallet.isHide) {
                it[isHide] = wallet.isHide
            }
            if (currentWallet.limit != wallet.limit) {
                it[limit] = wallet.limit
            }
            if (currentWallet.name != wallet.name) {
                it[name] = wallet.name
            }
        }
        true
    }

    suspend fun deleteWallet(walletId: Long, userId: Long): Boolean {
        return dbQuery {
            val result = Wallets.deleteWhere { (id eq walletId) and (Wallets.userId eq userId) }
            result > 0
        }
    }

    suspend fun getById(walletId: Long, userId: Long): ExposedWallet? {
        return dbQuery {
            getWallet(walletId, userId)
        }
    }

    private fun getWallet(walletId: Long, userId: Long): ExposedWallet? {
        return Join(
            table = Wallets, CourceService.Courses,
            onColumn = Wallets.currencyId, otherColumn = CourceService.Courses.id,
            joinType = JoinType.INNER
        ).select {
            (Wallets.id eq walletId) and (Wallets.userId eq userId)
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
                    name = it[CourceService.Courses.name],
                    course = it[CourceService.Courses.cource],
                    fullName = it[CourceService.Courses.fullName],
                    fullListName = it[CourceService.Courses.fullListName],
                    icon = it[CourceService.Courses.icon],
                    isUp = it[CourceService.Courses.isUp]
                )
            )
        }.singleOrNull()
    }
}