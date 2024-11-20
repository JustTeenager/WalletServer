package com.example

import com.example.plugins.configureHTTP
import com.example.plugins.configureSecurity
import com.example.plugins.configureSerialization
import com.example.routers.*
import com.example.utils.connectDatabase
import com.example.utils.getEnvProperty
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*

fun main() {
    embeddedServer(
        Netty,
        port = 80,
        host = getEnvProperty("host"),
        module = Application::module
    ).start(wait = true)
}

fun Application.module() {
    val database = connectDatabase()
    configureSerialization()
    configureHTTP()
    configureSecurity()

    personRouting(database)
    mainScreenRouting(database)
    walletRouting(database)
    categoryRouting(database)
    courceRouting(database)
    transactionRouting(database)
}

