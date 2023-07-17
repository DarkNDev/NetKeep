package com.darkndev.netkeep.api

import com.darkndev.netkeep.models.AuthRequest
import com.darkndev.netkeep.models.AuthResponse
import com.darkndev.netkeep.models.Note
import com.darkndev.netkeep.utils.user.AuthResult
import com.darkndev.netkeep.utils.user.Resource
import com.darkndev.netkeep.utils.Urls.ALL_NOTES
import com.darkndev.netkeep.utils.Urls.SIGN_IN
import com.darkndev.netkeep.utils.Urls.SIGN_UP
import com.darkndev.netkeep.utils.Urls.UPDATE
import com.darkndev.netkeep.utils.response
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import javax.inject.Inject

class NotesApi @Inject constructor(
    private val client: HttpClient
) {

    suspend fun signUp(request: AuthRequest): AuthResult<String> =
        response(
            request = {
                client.post {
                    url(SIGN_UP)
                    contentType(ContentType.Application.Json)
                    setBody(request)
                }
            },
            success = {
                AuthResult.Authorized("Success")
            },
            error = { status, code, e ->
                val message = status?.bodyAsText() ?: code?.description
                ?: e.localizedMessage
                ?: "Unknown Error"
                AuthResult.Unauthorized(message)
            }
        )

    suspend fun signIn(request: AuthRequest): AuthResult<String> =
        response(
            request = {
                client.post {
                    url(SIGN_IN)
                    contentType(ContentType.Application.Json)
                    setBody(request)
                }
            },
            success = {
                val token = it.body<AuthResponse>().token
                AuthResult.Authorized(token)
            },
            error = { status, code, e ->
                val message = status?.bodyAsText() ?: code?.description
                ?: e.localizedMessage
                ?: "Unknown Error"
                AuthResult.Unauthorized(message)
            }
        )

    suspend fun getAllNotes(token: String): Resource<List<Note>> {
        return response(
            request = {
                client.get {
                    url(ALL_NOTES)
                    header("Authorization", "Bearer $token")
                }
            },
            success = {
                val notes = it.body<List<Note>>()
                Resource.Success(notes)
            },
            error = { _, _, e ->
                Resource.Error(e)
            }
        )
    }


    suspend fun uploadAllNotes(token: String, databaseNotes: List<Note>): Resource<String> {
        return response(
            request = {
                client.post {
                    url(UPDATE)
                    contentType(ContentType.Application.Json)
                    setBody(databaseNotes)
                    header("Authorization", "Bearer $token")
                }
            },
            success = {
                val status = it.body<String>()
                Resource.Success(status)
            },
            error = { _, _, e ->
                Resource.Error(e)
            }
        )
    }
}