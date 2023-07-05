package com.darkndev.netkeep.api

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.darkndev.netkeep.utils.Constants

class RetrieveWorkerReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val getNotesRequest = OneTimeWorkRequestBuilder<RetrieveWorker>()
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(context)
            .enqueueUniqueWork(
                Constants.INITIAL_SYNC_WORKER,
                ExistingWorkPolicy.REPLACE,
                getNotesRequest
            )
    }
}