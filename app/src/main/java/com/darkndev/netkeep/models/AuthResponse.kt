package com.darkndev.netkeep.models

import kotlinx.serialization.Serializable

@Serializable
data class AuthResponse(
    val token:String
)