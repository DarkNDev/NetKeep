package com.darkndev.netkeep.recyclerview

import androidx.recyclerview.widget.DiffUtil
import com.darkndev.netkeep.models.Note

object NoteDiffUtil : DiffUtil.ItemCallback<Note>() {
    override fun areItemsTheSame(oldItem: Note, newItem: Note) = oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: Note, newItem: Note) = oldItem == newItem
}