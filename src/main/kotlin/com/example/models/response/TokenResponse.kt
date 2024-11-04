package com.example.models.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class TokenResponse(
    @SerialName("token")
    val token: String
)