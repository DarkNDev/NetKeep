package com.darkndev.netkeep.database

import com.darkndev.netkeep.api.NotesApi
import com.darkndev.netkeep.models.AuthRequest
import com.darkndev.netkeep.models.Note
import com.darkndev.netkeep.utils.AuthResult
import javax.inject.Inject

class NoteRepository @Inject constructor(
    private val noteDao: NoteDao,
    private val notesApi: NotesApi,
    private val prefs: PreferencesManager
) {

    val allNotes = noteDao.getNotes()

    suspend fun signUp(username: String, password: String): AuthResult<String> {
        val result = notesApi.signUp(AuthRequest(username, password))
        if (result is AuthResult.Authorized) {
            return signIn(username, password)
        }
        return result
    }

    suspend fun signIn(username: String, password: String): AuthResult<String> =
        notesApi.signIn(AuthRequest(username, password))


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

    suspend fun sync() =
        notesApi.syncAllNotes()

    suspend fun serverAddNote(note: Note) =
        notesApi.addNote(note)

    suspend fun serverEditNote(note: Note) =
        notesApi.editNote(note)

    suspend fun serverDeleteNote(note: Note) =
        notesApi.deleteNote(note)
}