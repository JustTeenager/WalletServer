package com.example.database

import com.example.utils.dbQuery
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import java.math.BigDecimal

data class ExposedCource(
    val currencyId: Long,
    val name: String = "",
    val course: BigDecimal = BigDecimal("0"),
    val fullName: String = "",
    val fullListName: String = "",
    val icon: String = "",
    val isUp: Boolean = false
)

class CourceService(database: Database) {
    object Cources : Table() {
        val id = long("id").autoIncrement()
        val name = varchar("name", 1)
        val cource = decimal("cource", 25, 5)
        val fullName = varchar("full_name", 250)
        val fullListName = varchar("full_list_name", 250)
        val icon = varchar("icon", 1)
        val isUp = bool("is_up")

        override val primaryKey = PrimaryKey(id)
    }

    init {
        transaction(database) {
            SchemaUtils.create(Cources)
        }
    }

    suspend fun getCourcesWithoutRub(): List<ExposedCource> {
        return dbQuery {
            Cources.select {
                Cources.name neq "RUB"
            }.map {
                ExposedCource(
                    it[Cources.id],
                    it[Cources.name],
                    it[Cources.cource],
                    it[Cources.fullName],
                    it[Cources.fullListName],
                    it[Cources.icon],
                    it[Cources.isUp]
                )
            }
        }
    }
}