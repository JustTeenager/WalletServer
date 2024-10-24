package com.example

import com.example.plugins.configureHTTP
import com.example.plugins.configureRouting
import com.example.plugins.configureSecurity
import com.example.plugins.configureSerialization
import com.example.routers.personRouting
import com.example.utils.connectDatabase
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
fun main() {
    embeddedServer(
        Netty,
        port = 8080,
        host = "localhost",
        module = Application::module
    ).start(wait = true)
}

fun Application.module() {
    val database = connectDatabase()
    configureSecurity()
    configureSerialization()
    configureHTTP()
    configureRouting()

    personRouting(database)
}
