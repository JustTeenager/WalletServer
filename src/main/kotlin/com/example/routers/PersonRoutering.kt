package com.example.routers

import com.auth0.jwt.JWT
import com.example.database.ExposedUser
import com.example.database.UserService
import com.example.plugins.createJWT
import com.example.request.UserRequest
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.Database
import kotlin.random.Random

fun Application.personRouting(database: Database) {
    routing {
        val userService = UserService(database)
        post("/person") {
            val user = call.receive<UserRequest>()
            val userId = userService.read(user.email)?.id ?: userService.create(user.toExposeUser())
            call.respondText("Hello World!")
            val token = this@personRouting.createJWT(userId)
            call.respond(token)
        }
    }
}

private fun UserRequest.toExposeUser() = ExposedUser(
    id = Random.nextLong(),
    email = email
)
