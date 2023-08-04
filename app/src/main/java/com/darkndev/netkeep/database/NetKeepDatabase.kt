package com.darkndev.netkeep.database

import android.app.Application
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.darkndev.netkeep.models.Note
import javax.inject.Inject

@Database(entities = [Note::class], version = 1, exportSchema = false)
abstract class NetKeepDatabase : RoomDatabase() {

    abstract fun noteDao(): NoteDao

    class Callback @Inject constructor(
        private val application: Application
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
        }
    }
}