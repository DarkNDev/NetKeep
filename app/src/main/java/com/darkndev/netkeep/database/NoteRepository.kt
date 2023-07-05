package com.darkndev.netkeep.database

import com.darkndev.netkeep.api.NotesApi
import com.darkndev.netkeep.models.Note
import javax.inject.Inject

class NoteRepository @Inject constructor(
    private val noteDao: NoteDao,
    private val notesApi: NotesApi
) {

    val allNotes = noteDao.getNotes()

    suspend fun upsertNotes(notes: List<Note>) {
        noteDao.upsertNote(notes)
    }

    suspend fun deleteNote(note: Note) {
        noteDao.deleteNote(note)
    }

    suspend fun sync() =
        notesApi.syncAllNotes()
}