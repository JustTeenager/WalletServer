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

/**
 * Метод запуска сервера
 */
fun main() {
    embeddedServer(
        Netty,
        port = 80,
        host = getEnvProperty("host"),
        module = Application::module
    ).start(wait = true)
}

/**
 * Метод "собирает" все созданные нами ручки, делает их частью сервера
 */
fun Application.module() {
    val database = connectDatabase()
    /**
     *  Настройки сериализации, безопасности, HTTP-формата
     */
    configureSerialization()
    configureHTTP()
    configureSecurity()

    /**
     * Настройки созданных ручек
     */
    personRouting(database)
    mainScreenRouting(database)
    walletRouting(database)
    categoryRouting(database)
    courceRouting(database)
    transactionRouting(database)
    cartInfoRouting(database)
}

