package com.example.utils

import org.jetbrains.exposed.sql.Database

fun connectDatabase(): Database {
    return Database.connect(
        url = "",
        user = "",
        driver = "",
        password = "",
    )
}