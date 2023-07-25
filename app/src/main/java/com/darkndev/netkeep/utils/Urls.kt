package com.darkndev.netkeep.utils

import com.darkndev.netkeep.BuildConfig

object Urls {
    private const val BASE_URL = BuildConfig.BASE_URL

    const val SIGN_UP = "$BASE_URL/signup"
    const val SIGN_IN = "$BASE_URL/signin"

    const val ALL_NOTES = "$BASE_URL/notes"
    const val UPDATE = "$BASE_URL/update"
}