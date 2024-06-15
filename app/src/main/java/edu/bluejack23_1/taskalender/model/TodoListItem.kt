package edu.bluejack23_1.taskalender.model

import com.google.firebase.Timestamp

data class TodoListItem(
    var id : String,
    val title : String,
    val deadline: Timestamp,
    val menu: String,
    var status: Boolean
) {
    // Default no-argument constructor
    constructor() : this("","",  Timestamp.now(), "", false)
}