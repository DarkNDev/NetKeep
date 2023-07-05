package com.darkndev.netkeep.api

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.darkndev.netkeep.R
import com.darkndev.netkeep.database.NoteDao
import com.darkndev.netkeep.utils.Constants
import com.darkndev.netkeep.utils.Resource
import com.darkndev.netkeep.utils.errorMessage
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

class RetrieveWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted workerParams: WorkerParameters,
    @Assisted private val notesApi: NotesApi,
    @Assisted private val noteDao: NoteDao
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return when (val resource = notesApi.getAllNotes()) {
            is Resource.Error -> {
                showNotification(true, "Retrieve Failed: ${errorMessage { resource.error }}")
                Result.failure()
            }

            is Resource.Success -> {
                resource.data?.let { noteDao.upsertNote(it) }
                context.sendBroadcast(Intent(context, SyncWorkerReceiver::class.java))
                showNotification(false, "Retrived All Notes")
                Result.success()
            }
        }
    }

    private fun showNotification(action: Boolean, message: String) {
        val notificationManager =
            context.getSystemService(NotificationManager::class.java)

        val notification = NotificationCompat.Builder(context, Constants.NOTIFICATION_CHANNEL_1)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentText(message)
            .setContentTitle("Retrieval")
            .setAutoCancel(true)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .setBigContentTitle("Retrieval")
                    .bigText(message)
            )

        if (action) {
            notification.addAction(
                R.drawable.ic_launcher_foreground, "Retry", PendingIntent.getBroadcast(
                    context,
                    1,
                    Intent(context, RetrieveWorkerReceiver::class.java),
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
            )
        }

        notificationManager.notify(1, notification.build())
    }
}