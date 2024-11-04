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
    object Courses : Table() {
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
            SchemaUtils.create(Courses)
        }
    }

    suspend fun getCoursesWithoutRub(): List<ExposedCource> {
        return dbQuery {
            Courses.select {
                Courses.name neq "RUB"
            }.map {
                ExposedCource(
                    it[Courses.id],
                    it[Courses.name],
                    it[Courses.cource],
                    it[Courses.fullName],
                    it[Courses.fullListName],
                    it[Courses.icon],
                    it[Courses.isUp]
                )
            }
        }
    }
}