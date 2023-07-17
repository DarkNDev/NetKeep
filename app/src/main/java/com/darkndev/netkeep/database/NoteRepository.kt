package com.darkndev.netkeep.database

import android.content.Context
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.darkndev.netkeep.api.NotesApi
import com.darkndev.netkeep.api.UploadWorker
import com.darkndev.netkeep.models.AuthRequest
import com.darkndev.netkeep.models.Note
import com.darkndev.netkeep.utils.user.AuthResult
import com.darkndev.netkeep.utils.Constants
import com.darkndev.netkeep.utils.user.Resource
import com.darkndev.netkeep.utils.errorMessage
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import java.time.Duration
import javax.inject.Inject

class NoteRepository @Inject constructor(
    private val noteDao: NoteDao,
    private val notesApi: NotesApi,
    private val prefs: PreferencesManager,
    @ApplicationContext private val context: Context
) {

    val allNotes = noteDao.getNotes()

    suspend fun signUp(username: String, password: String): AuthResult<String> {
        val result = notesApi.signUp(AuthRequest(username, password))
        if (result is AuthResult.Authorized) {
            return signIn(username, password)
        }
        return result
    }

    suspend fun signIn(username: String, password: String): AuthResult<String> {
        val result = notesApi.signIn(AuthRequest(username, password))
        return if (result is AuthResult.Authorized) {
            val token = result.data
            if (!token.isNullOrBlank()) {
                when (val resource = notesApi.getAllNotes(token)) {
                    is Resource.Error -> {
                        AuthResult.Unauthorized(errorMessage { resource.error })
                    }

                    is Resource.Success -> {
                        val notes = resource.data ?: return AuthResult.UnknownError()
                        prefs.updateToken(token)
                        noteDao.upsertNote(notes)
                        startUpdateWorker()
                        result
                    }
                }
            } else {
                AuthResult.Unauthorized("No Token Found")
            }
        } else {
            result
        }
    }

    suspend fun upsertNotes(notes: List<Note>) {
        noteDao.upsertNote(notes)
    }

    suspend fun deleteNote(note: Note) {
        noteDao.deleteNote(note)
    }

    suspend fun signOut() {
        noteDao.deleteAllNotes()
        prefs.updateToken("")
    }

    suspend fun uploadAllNotes(): Resource<String> {
        val token = prefs.token.first()
        return if (!token.isNullOrBlank()) {
            val notes = allNotes.first()
            notesApi.uploadAllNotes(token, notes)
        } else {
            Resource.Error(Throwable("No Token Found"))
        }
    }

    private fun startUpdateWorker() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresBatteryNotLow(true)
            .build()

        val uploadWorkRequest =
            PeriodicWorkRequestBuilder<UploadWorker>(Duration.ofHours(12))
                .setConstraints(constraints)
                .setInitialDelay(Duration.ofMinutes(5))
                .setBackoffCriteria(BackoffPolicy.LINEAR, Duration.ofMinutes(5))
                .build()

        WorkManager.getInstance(context)
            .enqueueUniquePeriodicWork(
                Constants.PERIODIC_SYNC_WORKER,
                ExistingPeriodicWorkPolicy.KEEP,
                uploadWorkRequest
            )
    }
}