package com.darkndev.netkeep.api

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.darkndev.netkeep.database.NoteDao
import com.darkndev.netkeep.database.PreferencesManager
import javax.inject.Inject

class NetKeepWorkerFactory @Inject constructor(
    private val notesApi: NotesApi,
    private val noteDao: NoteDao,
    private val prefs: PreferencesManager
) : WorkerFactory() {
    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker? =
        when (workerClassName) {
            UploadWorker::class.java.name -> UploadWorker(
                appContext,
                workerParameters,
                notesApi,
                noteDao,
                prefs
            )

            else -> null
        }

}