package com.darkndev.netkeep.api

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.darkndev.netkeep.R
import com.darkndev.netkeep.utils.Constants.NOTIFICATION_CHANNEL_1
import com.darkndev.netkeep.utils.Resource
import com.darkndev.netkeep.utils.errorMessage
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted workerParams: WorkerParameters,
    @Assisted private val notesApi: NotesApi
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return when (val resource = notesApi.syncAllNotes()) {
            is Resource.Error -> {
                showNotification(true, "Sync Failed: ${errorMessage { resource.error }}")
                Result.failure()
            }

            is Resource.Success -> {
                showNotification(false, resource.data!!)
                Result.success()
            }
        }
    }

    private fun showNotification(action: Boolean, message: String) {
        val notificationManager =
            context.getSystemService(NotificationManager::class.java)

        val notification = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_1)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentText(message)
            .setContentTitle("Sync")
            .setAutoCancel(true)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .setBigContentTitle("Sync")
                    .bigText(message)
            )

        if (action) {
            notification.addAction(
                R.drawable.ic_launcher_foreground, "Retry", PendingIntent.getBroadcast(
                    context,
                    2,
                    Intent(context, SyncWorkerReceiver::class.java),
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
            )
        }

        notificationManager.notify(2, notification.build())
    }
}