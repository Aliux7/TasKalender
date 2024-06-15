package edu.bluejack23_1.taskalender.model

import com.google.firebase.Timestamp

data class User(
    val email : String,
    val phone: String,
    val username: String,
    val dob: Timestamp
) {
    // Default no-argument constructor
    constructor() : this("", "",  "", Timestamp.now())
}