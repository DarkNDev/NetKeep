package com.darkndev.netkeep.recyclerview

import androidx.recyclerview.widget.RecyclerView
import com.darkndev.netkeep.databinding.LayoutNoteCardBinding
import com.darkndev.netkeep.models.Note
import com.darkndev.netkeep.utils.then

class NoteViewHolder(
    private val binding: LayoutNoteCardBinding,
    private val onNoteClick: (Int) -> Unit
) :
    RecyclerView.ViewHolder(binding.root) {

    init {
        binding.root.setOnClickListener {
            val position = bindingAdapterPosition
            (position != RecyclerView.NO_POSITION) then {
                onNoteClick(position)
            }
        }
    }

    fun bind(note: Note) {
        binding.apply {
            titleCard.text = note.title
            contentCard.text = note.content
        }
    }
}