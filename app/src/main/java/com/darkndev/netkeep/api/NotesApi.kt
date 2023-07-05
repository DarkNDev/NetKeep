package com.darkndev.netkeep.api

import android.util.Log
import com.darkndev.netkeep.database.NoteDao
import com.darkndev.netkeep.models.Note
import com.darkndev.netkeep.utils.Resource
import com.darkndev.netkeep.utils.Urls.ALL_NOTES
import com.darkndev.netkeep.utils.Urls.SYNC
import com.darkndev.netkeep.utils.response
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class NotesApi @Inject constructor(
    private val client: HttpClient,
    private val noteDao: NoteDao
) {

    suspend fun getAllNotes(): Resource<List<Note>> = response(
        request = {
            client.get {
                url(ALL_NOTES)
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


    suspend fun syncAllNotes(): Resource<String> {
        val databaseNotes = noteDao.getNotes().first()
        return response(
            request = {
                client.post {
                    url(SYNC)
                    contentType(ContentType.Application.Json)
                    setBody(databaseNotes)
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