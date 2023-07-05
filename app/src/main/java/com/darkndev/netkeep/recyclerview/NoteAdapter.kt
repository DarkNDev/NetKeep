package com.darkndev.netkeep.recyclerview

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.ListAdapter
import com.darkndev.netkeep.databinding.LayoutNoteCardBinding
import com.darkndev.netkeep.models.Note
import com.darkndev.netkeep.utils.then

class NoteAdapter(private val onNoteClick: (Note) -> Unit) :
    ListAdapter<Note, NoteViewHolder>(NoteDiffUtil), Filterable {

    private lateinit var notesFull: List<Note>

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = NoteViewHolder(
        LayoutNoteCardBinding.inflate(LayoutInflater.from(parent.context)),
        onNoteClick = { position ->
            val note = getItem(position)
            (note != null) then { onNoteClick(note) }
        })

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val note = getItem(position)
        (note != null) then { holder.bind(note) }
    }

    fun submitFilterableList(notes: List<Note>, afterSubmit: () -> Runnable) {
        submitList(notes, afterSubmit())
        notesFull = notes
    }

    override fun getFilter(): Filter {
        return FilterNotes()
    }

    inner class FilterNotes : Filter() {
        override fun performFiltering(constraint: CharSequence?): FilterResults {
            val filteredList = mutableListOf<Note>()

            if (constraint.isNullOrEmpty()) {
                filteredList.addAll(notesFull)
            } else {
                val filterPattern = constraint.toString().lowercase().trim()

                notesFull.forEach {
                    if (it.title.lowercase().contains(filterPattern)) {
                        filteredList.add(it)
                    }
                }
            }

            val results = FilterResults()
            results.values = filteredList

            return results
        }

        override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
            val filteredList = results?.values as List<Note>
            submitList(filteredList)
            return
        }
    }
}