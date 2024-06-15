package edu.bluejack23_1.taskalender.model

import com.google.firebase.Timestamp

sealed class NotesItem{
    data class NoteDetail(
    val noteID : String,
    val noteName: String,
    val noteContent: String,
    val noteFolderName: String,
    val noteCreatedDate: Timestamp
    ) {
        // Default no-argument constructor
        constructor() : this("", "", "", "", Timestamp.now())
    }
    data class NoteHeader(
        val date: Timestamp
    )

}