package com.example.database

import com.example.utils.dbQuery
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

@Serializable
data class ExposedUser(val id: Long, val email: String)

/**
 * Сервис пользователей в базе данных
 * Создает базу данных пользователей и возвращает данные по нужному пользователю
 */
class UserService(database: Database) {
    object Users : Table() {
        val id = long("id").autoIncrement()
        val email = varchar("email", length = 50)

        override val primaryKey = PrimaryKey(id)
    }

    init {
        transaction(database) {
            SchemaUtils.create(Users)
        }
    }

    /**
     * Создает пользователя на основе пришедних данных
     */
    suspend fun create(user: ExposedUser): Long = dbQuery {
        Users.insert {
            it[email] = user.email
        }[Users.id]
    }

    /**
     * Достает данные о пользователе по запросу для генерации JWTKey (авторизация)
     */
    suspend fun read(email: String): ExposedUser? {
        return dbQuery {
            Users.select { Users.email eq email }.map { ExposedUser(it[Users.id], it[Users.email]) }.singleOrNull()
        }
    }
}