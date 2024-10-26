package com.example.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserRequest(
    @SerialName("email")
    val email: String
)