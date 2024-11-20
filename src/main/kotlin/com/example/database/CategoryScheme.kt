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

class CategoryService(database: Database) {

    object Category : Table() {
        val id = long("id").autoIncrement()
        val type = integer("type")
        val operation = text("name")
        val idIcon = integer("icon_id")
        val userId = long("user_id").nullable()

        override val primaryKey = PrimaryKey(id)
    }

    init {
        transaction(database) {
            SchemaUtils.create(Category)
        }
    }

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