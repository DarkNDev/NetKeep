package com.darkndev.netkeep.utils

object Urls {
    private const val BASE_URL = "http://192.168.1.10:8080"

    const val SIGN_UP = "$BASE_URL/signup"
    const val SIGN_IN = "$BASE_URL/signin"

    const val ALL_NOTES = "$BASE_URL/notes"
    const val SYNC = "$BASE_URL/sync"
    const val ADD = "$BASE_URL/add"
    const val EDIT = "$BASE_URL/edit"
    const val DELETE = "$BASE_URL/delete"
}