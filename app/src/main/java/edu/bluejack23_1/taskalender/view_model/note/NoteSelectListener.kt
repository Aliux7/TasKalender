package edu.bluejack23_1.taskalender.view_model.note

import edu.bluejack23_1.taskalender.model.FolderItem
import edu.bluejack23_1.taskalender.model.NotesItem

interface NoteSelectListener {
    fun onItemClicked(noteItem: NotesItem.NoteDetail);
}