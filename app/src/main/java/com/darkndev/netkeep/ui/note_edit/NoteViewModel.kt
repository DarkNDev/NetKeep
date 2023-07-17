package com.darkndev.netkeep.ui.note_edit

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.darkndev.netkeep.database.NoteRepository
import com.darkndev.netkeep.models.Note
import com.darkndev.netkeep.utils.user.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class NoteViewModel @Inject constructor(
    private val state: SavedStateHandle,
    private val repository: NoteRepository
) : ViewModel() {

    val note = state.get<Note>("note")

    var title = state.get<String>("title") ?: note?.title ?: ""
        set(value) {
            field = value
            state["title"] = value
        }

    var content = state.get<String>("content") ?: note?.content ?: ""
        set(value) {
            field = value
            state["content"] = value
        }

    fun saveClicked() = viewModelScope.launch {
        if (title.isBlank() || content.isBlank()) return@launch noteChannel.send(Event.ShowMessage("Check Fields"))

        if (note == null) {
            val insertNote = Note(Random.nextInt(), title, content)
            repository.upsertNotes(listOf(insertNote))
        } else {
            val updateNote = note.copy(
                id = note.id,
                title = title,
                content = content
            )
            repository.upsertNotes(listOf(updateNote))
        }
        noteChannel.send(Event.Navigate)
    }

    fun deleteClicked() = viewModelScope.launch {
        if (note != null) {
            repository.deleteNote(note)
            noteChannel.send(Event.Navigate)
        }
    }

    private val noteChannel = Channel<Event>()
    val noteEvent = noteChannel.receiveAsFlow()
}