package com.darkndev.netkeep.api

import android.app.Application
import android.content.Intent
import android.util.Log
import com.darkndev.netkeep.database.NoteDao
import com.darkndev.netkeep.database.PreferencesManager
import com.darkndev.netkeep.models.AuthRequest
import com.darkndev.netkeep.models.AuthResponse
import com.darkndev.netkeep.models.Note
import com.darkndev.netkeep.utils.AuthResult
import com.darkndev.netkeep.utils.Resource
import com.darkndev.netkeep.utils.Urls.ADD
import com.darkndev.netkeep.utils.Urls.ALL_NOTES
import com.darkndev.netkeep.utils.Urls.DELETE
import com.darkndev.netkeep.utils.Urls.EDIT
import com.darkndev.netkeep.utils.Urls.SIGN_IN
import com.darkndev.netkeep.utils.Urls.SIGN_UP
import com.darkndev.netkeep.utils.Urls.SYNC
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
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class NotesApi @Inject constructor(
    private val client: HttpClient,
    private val noteDao: NoteDao,
    private val prefs: PreferencesManager,
    private val application: Application
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
                prefs.updateToken(token)
                application.sendBroadcast(Intent(application, RetrieveWorkerReceiver::class.java))
                AuthResult.Authorized("Authorised")
            },
            error = { status, code, e ->
                val message = status?.bodyAsText() ?: code?.description
                ?: e.localizedMessage
                ?: "Unknown Error"
                AuthResult.Unauthorized(message)
            }
        )

    suspend fun getAllNotes(): Resource<List<Note>> {
        val token =
            prefs.token.first() ?: return Resource.Error(Throwable("No Token Found"))
        return response(
            request = {
                client.get {
                    url(ALL_NOTES)
                    header("Authorization", "Bearer $token")
                }
            },
            success = {
                val statusCode = it.status
                Log.i("NotesApi", "Success: $statusCode")
                val notes = it.body<List<Note>>()
                Resource.Success(notes)
            },
            error = { _, _, e ->
                Resource.Error(e)
            }
        )
    }


    suspend fun syncAllNotes(): Resource<String> {
        val token =
            prefs.token.first() ?: return Resource.Error(Throwable("No Token Found"))
        val databaseNotes = noteDao.getNotes().first()
        return response(
            request = {
                client.post {
                    url(SYNC)
                    contentType(ContentType.Application.Json)
                    setBody(databaseNotes)
                    header("Authorization", "Bearer $token")
                }
            },
            success = {
                val statusCode = it.status

                Log.i("NotesApi", "Success: $statusCode")
                val status = it.body<String>()
                Resource.Success(status)
            },
            error = { _, _, e ->
                Resource.Error(e)
            }
        )
    }

    suspend fun addNote(note: Note): Resource<String> {
        val token =
            prefs.token.first() ?: return Resource.Error(Throwable("No Token Found"))
        return response(
            request = {
                client.post {
                    url(ADD)
                    contentType(ContentType.Application.Json)
                    setBody(note)
                    header("Authorization", "Bearer $token")
                }
            },
            success = {
                val statusCode = it.status

                Log.i("NotesApi", "Success: $statusCode")
                val status = it.body<String>()
                Resource.Success(status)
            },
            error = { _, _, e ->
                Resource.Error(e)
            }
        )
    }

    suspend fun editNote(note: Note): Resource<String> {
        val token =
            prefs.token.first() ?: return Resource.Error(Throwable("No Token Found"))
        return response(
            request = {
                client.post {
                    url(EDIT)
                    contentType(ContentType.Application.Json)
                    setBody(note)
                    header("Authorization", "Bearer $token")
                }
            },
            success = {
                val statusCode = it.status

                Log.i("NotesApi", "Success: $statusCode")
                val status = it.body<String>()
                Resource.Success(status)
            },
            error = { _, _, e ->
                Resource.Error(e)
            }
        )
    }

    suspend fun deleteNote(note: Note): Resource<String> {
        val token =
            prefs.token.first() ?: return Resource.Error(Throwable("No Token Found"))
        return response(
            request = {
                client.post {
                    url(DELETE)
                    contentType(ContentType.Application.Json)
                    setBody(note)
                    header("Authorization", "Bearer $token")
                }
            },
            success = {
                val statusCode = it.status

                Log.i("NotesApi", "Success: $statusCode")
                val status = it.body<String>()
                Resource.Success(status)
            },
            error = { _, _, e ->
                Resource.Error(e)
            }
        )
    }

}