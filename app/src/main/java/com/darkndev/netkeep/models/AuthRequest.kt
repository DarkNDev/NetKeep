package com.darkndev.netkeep.models

import kotlinx.serialization.Serializable

@Serializable
data class AuthRequest(
    val username:String,
    val password:String
)
