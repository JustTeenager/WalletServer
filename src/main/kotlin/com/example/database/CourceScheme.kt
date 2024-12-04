package com.example.database

import com.example.utils.dbQuery
import org.jetbrains.exposed.sql.*
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

/**
 * Сервис, ответственный за работу с курсами валют
 * Несет в себе таблицу бд для курсов, заполняет ее
 * Позволяет достать список курсов или какой-то конкретный курс
 */
class CourceService(database: Database) {
    companion object {
        private const val RUB_NAME = "RUB"
    }

    /**
     * Объявление таблицы курсов в бд
     * курсы хранятся относительно рубля (соответственно курс самого рубля = 1)
     */
    object Courses : Table() {
        val id = long("id").autoIncrement()
        val name = varchar("name", 3)
        val cource = decimal("cource", 25, 5)
        val fullName = varchar("full_name", 250)
        val fullListName = varchar("full_list_name", 250)
        val icon = varchar("icon", 1)
        val isUp = bool("is_up")

        override val primaryKey = PrimaryKey(id)
    }

    /**
     * Создание таблицы курсов
     */
    init {
        transaction(database) {
            SchemaUtils.create(Courses)
        }
    }

    /**
     * Метод возвращает список курсов
     */
    suspend fun getCources(): List<ExposedCource> {
        return dbQuery {
            Courses
                .selectAll()
                .map {
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

    /**
     * Метод возвращает список курсов, исключая рубль
     */
    suspend fun getCoursesWithoutRub(): List<ExposedCource> {
        return dbQuery {
            Courses.select {
                Courses.name neq RUB_NAME
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

    /**
     * Метод возвращает список курсов
     */
    suspend fun getCourceById(id: Long): ExposedCource? {
        return dbQuery {
            Courses.select { Courses.id eq id }.map {
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
        }.singleOrNull()
    }
}