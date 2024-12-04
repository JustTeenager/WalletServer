package com.example.routers

import com.example.database.CourceService
import com.example.database.ExposedCource
import com.example.models.response.main_screen.CurrencyResponse
import com.example.utils.toStringWithFormat
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.Database

/**
 * Создание ручек для работы с курсами
 */
fun Application.courceRouting(database: Database) {

    routing {
        authenticate("auth-jwt") {
            val courceService = CourceService(database)
            /**
             * Возвращаем данные из courceService
             */
            get("/currencies") {
                call.respond(courceService.getCources().map { it.toCurrencyResponse() })
            }
        }
    }
}


fun ExposedCource.toCurrencyResponse() = CurrencyResponse(
    currencyId,
    name,
    course.toStringWithFormat(),
    fullName,
    fullListName,
    icon,
    isUp
)