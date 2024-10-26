package com.example.database

import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction

@Serializable
data class ExposedUser(
    val id: Long,
    val email: String
)

class UserService(database: Database) {
    object Users : Table() {
        val id = long("id").autoIncrement()
        val email = varchar("email", length = 250)

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

    private suspend fun <T> dbQuery(block: suspend () -> T): T = newSuspendedTransaction(Dispatchers.IO) { block() }
}
