package com.example.utils

import java.io.FileInputStream
import java.util.*

fun getEnvProperty(name: String): String {
    return System.getenv(name) ?: run {
        val properties = Properties()
        properties.load(FileInputStream("local.properties"))
        properties.getProperty("host")
    } ?: "localhost"
}