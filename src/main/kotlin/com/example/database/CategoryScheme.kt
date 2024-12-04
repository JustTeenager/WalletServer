package com.example.database

import com.example.utils.dbQuery
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

data class ExchangeCategory(
    val id: Long,
    val type: Int,
    val operation: String,
    val iconId: Int,
)


/**
 * Сервис категорий в базе данных
 * Несет в себе таблицу в БД Категорий, заполняет ее стандартными категориями
 * Позволяет получать существующие категории и создавать новые
 */
class CategoryService(database: Database) {

    /**
     * Объявление таблицы категорий в БД
     * type - расход/доход
     * operation - название категории
     */
    object Category : Table() {
        val id = long("id").autoIncrement()
        val type = integer("type")
        val operation = text("name")
        val idIcon = integer("icon_id")
        val userId = long("user_id").nullable()

        override val primaryKey = PrimaryKey(id)
    }

    /**
     * Заполнение базы данных стандартными категориями
     */
    init {
        transaction(database) {
            SchemaUtils.create(Category)
        }
    }

    /**
     * Метод возвращает список всех доступных категорий
     */
    suspend fun getAll(userId: Long): List<ExchangeCategory> {
        return dbQuery {
            Category.select { Category.userId eq null or (Category.userId eq userId) }.map {
                ExchangeCategory(
                    id = it[Category.id],
                    type = it[Category.type],
                    operation = it[Category.operation],
                    iconId = it[Category.idIcon]
                )
            }
        }
    }

    /**
     * Создание новой категории на основе введенных данных
     * @param category данные о новой категории
     */
    suspend fun create(category: ExchangeCategory, userId: Long): ExchangeCategory {
        return dbQuery {
            val id = Category.insert {
                it[type] = category.type
                it[operation] = category.operation
                it[idIcon] = category.iconId
                it[Category.userId] = userId
            }[Category.id]
            category.copy(id = id)
        }
    }
}