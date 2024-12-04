package com.example.routers

import com.example.database.CategoryService
import com.example.database.ExchangeCategory
import com.example.models.request.CategoryRequest
import com.example.models.response.CategoryResponse
import com.example.utils.toBoolean
import com.example.utils.toInt
import kotlin.random.Random
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.Database

/**
 * Создание ручек для работы с категориями
 */
fun Application.categoryRouting(database: Database) {
    routing {
        authenticate("auth-jwt") {
            val categoryService = CategoryService(database)
            /**
             * Получение списка категорий
             * берем категории из categoryService и возвращем по ним response
             */
            get("/categories") {
                val principal = call.authentication.principal<JWTPrincipal>()
                val userId = principal?.payload?.getClaim("user_id")?.asLong()!!
                val categories = categoryService.getAll(userId)
                call.respond(categories.map { it.toResponse() })
            }

            /**
             * Создание категории
             * Данные из запроса связываем с userId и отправляем базу данных через categoryService
             */
            post("/categories") {
                val principal = call.authentication.principal<JWTPrincipal>()
                val userId = principal?.payload?.getClaim("user_id")?.asLong()!!
                val request = call.receive<CategoryRequest>()
                val category = categoryService.create(request.toExchange(), userId)
                call.respond(category.toResponse())
            }
        }
    }
}

private fun ExchangeCategory.toResponse() = CategoryResponse(
    id = this.id,
    type = this.type.toBoolean(),
    operation = this.operation,
    idIcon = this.iconId
)

private fun CategoryRequest.toExchange() = ExchangeCategory(
    id = Random.nextLong(),
    type = this.type.toInt(),
    operation = this.operation,
    iconId = this.idIcon
)