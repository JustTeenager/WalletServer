package com.example.database

import com.example.utils.dbQuery
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

@Serializable
data class ExposedUser(val id: Long, val email: String)

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

    suspend fun create(user: ExposedUser): Long = dbQuery {
        Users.insert {
            it[email] = user.email
        }[Users.id]
    }

    suspend fun read(email: String): ExposedUser? {
        return dbQuery {
            Users.select { Users.email eq email }.map { ExposedUser(it[Users.id], it[Users.email]) }.singleOrNull()
        }
    }
}