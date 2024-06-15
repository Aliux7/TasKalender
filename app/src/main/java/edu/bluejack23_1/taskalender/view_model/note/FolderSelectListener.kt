package edu.bluejack23_1.taskalender.view_model.note

import edu.bluejack23_1.taskalender.model.FolderItem

interface FolderSelectListener {
    fun onItemClicked(folderItem: FolderItem);
}