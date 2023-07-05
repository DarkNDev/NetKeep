package com.darkndev.netkeep.api

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.darkndev.netkeep.database.NoteDao
import javax.inject.Inject

class NetKeepWorkerFactory @Inject constructor(
    private val notesApi: NotesApi,
    private val noteDao: NoteDao
) : WorkerFactory() {
    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker? =
        when (workerClassName) {
            SyncWorker::class.java.name -> SyncWorker(
                appContext,
                workerParameters,
                notesApi
            )

            RetrieveWorker::class.java.name -> RetrieveWorker(
                appContext,
                workerParameters,
                notesApi,
                noteDao
            )

            else -> null
        }

}