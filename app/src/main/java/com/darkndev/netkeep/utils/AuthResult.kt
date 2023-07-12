package com.darkndev.netkeep.utils

sealed class AuthResult<T>(val data: T? = null) {
    class Authorized<T>(data: T) : AuthResult<T>(data)
    class Unauthorized<T>(data: T) : AuthResult<T>(data)
    class UnknownError<T> : AuthResult<T>()
}
