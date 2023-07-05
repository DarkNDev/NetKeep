package com.darkndev.netkeep.models

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Entity(tableName = "note_table")
@Parcelize
@Serializable
data class Note(
    @PrimaryKey(autoGenerate = false)
    val id: Int,
    val title: String,
    val content: String
) : Parcelable
