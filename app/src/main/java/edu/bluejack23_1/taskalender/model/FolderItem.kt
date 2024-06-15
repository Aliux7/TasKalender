package edu.bluejack23_1.taskalender.model

import com.google.firebase.Timestamp

data class FolderItem(
    private var id: String,
    private var name: String,
    private var numOfNotes: Int,
    private var userID: String)
{
    constructor() : this("", "",0, "")
    fun getId(): String{
        return id
    }

    fun setId(newId: String){
        id = newId
    }

    fun getName(): String {
        return name
    }

    fun setName(newName: String) {
        name = newName
    }

    fun getNumOfNotes(): Int{
        return numOfNotes
    }

    fun setNumOfNotes(newNumOfNotes: Int){
        numOfNotes = newNumOfNotes
    }

    fun getUserID(): String{
        return userID
    }

    fun setUserID(newUserID: String){
        userID = newUserID
    }
}