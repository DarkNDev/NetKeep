package com.darkndev.netkeep.api

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.darkndev.netkeep.utils.Constants
import java.time.Duration

class SyncWorkerReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresBatteryNotLow(true)
            .build()

        val syncWorkRequest =
            PeriodicWorkRequestBuilder<SyncWorker>(Duration.ofHours(12))
                .setConstraints(constraints)
                .setInitialDelay(Duration.ofMinutes(1))
                .build()

        WorkManager.getInstance(context)
            .enqueueUniquePeriodicWork(
                Constants.PERIODIC_SYNC_WORKER,
                ExistingPeriodicWorkPolicy.KEEP,
                syncWorkRequest
            )
    }
}