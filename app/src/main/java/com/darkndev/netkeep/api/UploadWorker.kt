package com.darkndev.netkeep.api

import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.darkndev.netkeep.R
import com.darkndev.netkeep.database.NoteDao
import com.darkndev.netkeep.database.PreferencesManager
import com.darkndev.netkeep.utils.Constants.NOTIFICATION_CHANNEL_1
import com.darkndev.netkeep.utils.user.Resource
import com.darkndev.netkeep.utils.errorMessage
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first

@HiltWorker
class UploadWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted workerParams: WorkerParameters,
    @Assisted private val notesApi: NotesApi,
    @Assisted private val noteDao: NoteDao,
    @Assisted private val prefs: PreferencesManager
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val notes = noteDao.getNotes().first()
        val token = prefs.token.first()
        return if (!token.isNullOrBlank()) {
            when (val resource = notesApi.uploadAllNotes(token, notes)) {
                is Resource.Error -> {
                    showNotification("Upload Failed: ${errorMessage { resource.error }}")
                    Result.retry()
                }

                is Resource.Success -> {
                    showNotification(resource.data!!)
                    Result.success()
                }
            }
        } else {
            Result.failure()
        }
    }

    private fun showNotification(message: String) {
        val notificationManager =
            context.getSystemService(NotificationManager::class.java)

        val notification = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_1)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentText(message)
            .setContentTitle("Upload")
            .setAutoCancel(true)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .setBigContentTitle("Upload")
                    .bigText(message)
            )

        notificationManager.notify(2, notification.build())
    }
}