package com.example.utils

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import io.ktor.util.pipeline.*
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import java.math.BigDecimal
import java.math.RoundingMode

fun connectDatabase(): Database {
    return Database.connect(
        url = getEnvProperty("url"),
        user = getEnvProperty("user"),
        driver = "com.mysql.cj.jdbc.Driver",
        password = getEnvProperty("password"),
    )
}

suspend fun <T> dbQuery(block: suspend () -> T): T = newSuspendedTransaction(Dispatchers.IO) { block() }

fun BigDecimal.toStringWithFormat(): String {
    return this.setScale(2, RoundingMode.HALF_UP).toPlainString()
}

suspend inline fun PipelineContext<Unit, ApplicationCall>.withUserId(respond: (Long) -> Unit) {
    val principal = call.authentication.principal<JWTPrincipal>()
    val userId = principal?.payload?.getClaim("user_id")?.asLong() ?: run {
        call.respond(HttpStatusCode.Unauthorized, "Unauthorized")
        return
    }
    respond.invoke(userId)
}