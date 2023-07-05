package com.darkndev.netkeep.utils

import android.os.Build
import android.util.Log
import androidx.appcompat.widget.SearchView
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import com.darkndev.netkeep.api.RetrieveWorker
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.RedirectResponseException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpStatusCode
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime

infix fun <T> Boolean.then(param: () -> T): T? = if (this) param() else null

inline fun errorMessage(throwable: () -> Throwable?) =
    throwable()?.localizedMessage ?: "An unknown error occurred"

infix fun Long.isBefore(other: Long) = ZonedDateTime.ofInstant(
    Instant.ofEpochMilli(this),
    ZoneId.systemDefault()
).isBefore(
    ZonedDateTime.ofInstant(
        Instant.ofEpochMilli(other),
        ZoneId.systemDefault()
    )
)

inline fun <T> sdkVersion26AndAbove(onSdkVersion26: () -> T): T? {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        onSdkVersion26()
    } else null
}

inline fun <T> response(
    request: () -> HttpResponse,
    success: (HttpResponse) -> T,
    error: (HttpResponse?, HttpStatusCode?, Throwable) -> T
) = try {
    success(request())
} catch (e: RedirectResponseException) {
    //3xx - responses
    Log.e("AuthApiImpl", "RedirectResponseException: ", e)
    error(e.response, e.response.status, e)
} catch (e: ClientRequestException) {
    //4xx - responses
    Log.e("AuthApiImpl", "ClientRequestException: ", e)
    error(e.response, e.response.status, e)
} catch (e: ServerResponseException) {
    //5xx - responses
    Log.e("AuthApiImpl", "ServerResponseException: ", e)
    error(e.response, e.response.status, e)
} catch (e: Exception) {
    //others - responses
    Log.e("AuthApiImpl", "Exception: ", e)
    error(null, null, e)
}

inline fun SearchView.onQueryTextChange(crossinline listener: (String?) -> Unit) {
    this.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
        override fun onQueryTextSubmit(query: String?): Boolean {
            return true
        }

        override fun onQueryTextChange(newText: String?): Boolean {
            listener(newText)
            return true
        }
    })
}

