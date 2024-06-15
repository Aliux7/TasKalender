package edu.bluejack23_1.taskalender.view_model.note

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.icu.text.CaseMap.Fold
import android.provider.ContactsContract.CommonDataKinds.Note
import android.util.Log
import android.view.Gravity
import android.view.ViewGroup
import android.view.Window
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import edu.bluejack23_1.taskalender.R
import edu.bluejack23_1.taskalender.model.FolderItem
import edu.bluejack23_1.taskalender.model.NotesItem
import edu.bluejack23_1.taskalender.model.TodoListItem
import edu.bluejack23_1.taskalender.model.TransactionItem
import java.sql.Time
import java.util.Date

class NoteViewModel : ViewModel() {

    private val noteListLiveData: MutableLiveData<List<Any>> = MutableLiveData()
    private val folderListLiveData: MutableLiveData<List<FolderItem>> = MutableLiveData()
    private val db = FirebaseFirestore.getInstance()

    fun loadAllFolders(uid: String) {
        val foldersRef = db.collection("folders")
            .whereEqualTo("userID", uid)

        foldersRef.get()
            .addOnSuccessListener { querySnapshot ->
                val folderList = mutableListOf<FolderItem>()
                for (document in querySnapshot.documents) {
                    val id = document.id
                    val folderData = document.data
                    Log.d("testid", "fetch notes with folder with ID: $id")

                    if (folderData != null) {
                        val folder = FolderItem(
                            id,
                            folderData["name"] as String,
                            (folderData["numOfNotes"] as Long).toInt(),
                            folderData["userID"] as String
                        )
                        folderList.add(folder)
                    }
                }
                folderListLiveData.value = folderList
            }
    }



    fun loadAndSortNotes(folderID: String){
        val collectionRef = FirebaseFirestore.getInstance().collection("notes")
        val query = collectionRef.whereEqualTo("folderID", folderID)


        // Log the folder ID before proceeding with the update
        Log.d("fetchNotes", "fetch notes with folder with ID: $folderID")

        query.get()
            .addOnSuccessListener { documents ->
                val sortedDocuments = documents.sortedByDescending { it.getTimestamp("noteCreatedDate") }
                val noteList = mutableListOf<Any>()

                for (document in sortedDocuments) {
                    val note = document.toObject(NotesItem.NoteDetail::class.java)
                    if (note != null) {
                        val noteDate = note.noteCreatedDate
                        if (!noteList.any { it is NotesItem.NoteHeader && it.date.toDate().month == noteDate.toDate().month }) {
                            noteList.add(
                                NotesItem.NoteHeader(
                                    noteDate
                                )
                            )
                        }

                        // Add the transaction data
                        noteList.add(
                            NotesItem.NoteDetail(
                                document.id,
                                note.noteName,
                                note.noteContent,
                                note.noteFolderName,
                                note.noteCreatedDate
                            )
                        )
                    }
                }

                noteList.sortWith(compareByDescending<Any> {
                    when (it) {
                        is NotesItem.NoteDetail -> it.noteCreatedDate.toDate()
                        is NotesItem.NoteHeader -> it.date.toDate()
                        else -> TODO()
                    }
                })

                (noteListLiveData as MutableLiveData).value = noteList
            }
            .addOnFailureListener { exception ->
                println("Error: $exception")
            }
    }


    fun loadAndSortNotesByUID(uid: String){
        val foldersRef = db.collection("folders")
            .whereEqualTo("userID", uid)

        foldersRef.get()
            .addOnSuccessListener { collectionDocuments ->
                // Collect the IDs of collections where userID == uid
                val collectionIds = collectionDocuments.map { it.id }

                if(!collectionIds.isEmpty()){
                    // Now, we fetch notes where folderID is in the collectionIds list
                    val notesCollectionRef = FirebaseFirestore.getInstance().collection("notes")
                    val notesQuery = notesCollectionRef.whereIn("folderID", collectionIds)

                    notesQuery.get()
                        .addOnSuccessListener { noteDocuments ->
                            val sortedNotes = noteDocuments.sortedByDescending { it.getTimestamp("noteCreatedDate") }
                            val noteList = mutableListOf<Any>()

                            for (document in sortedNotes) {
                                val note = document.toObject(NotesItem.NoteDetail::class.java)
                                if (note != null) {
                                    noteList.add(
                                        NotesItem.NoteDetail(
                                            document.id,
                                            note.noteName,
                                            note.noteContent,
                                            note.noteFolderName,
                                            note.noteCreatedDate
                                        )
                                    )
                                }
                            }

                            noteList.sortWith(compareByDescending<Any> {
                                when (it) {
                                    is NotesItem.NoteDetail -> it.noteCreatedDate.toDate()
                                    is NotesItem.NoteHeader -> it.date.toDate()
                                    else -> TODO()
                                }
                            })

                            (noteListLiveData as MutableLiveData).value = noteList
                        }
                        .addOnFailureListener { exception ->
                            println("Error fetching notes: $exception")
                        }
                }

            }
    }

    fun getFolderListLiveData(): LiveData<List<FolderItem>> {
        return folderListLiveData
    }
    fun getNoteListLiveData(): LiveData<List<Any>> {
        return noteListLiveData
    }

    fun insertFolder(folderName: String, userID: String): Int {
        if(folderName.isEmpty()){
            //return err message
            return -1;
        }
        val folderData: Map<String, Any> = hashMapOf(
            "userID" to userID,
            "numOfNotes" to 0,
            "name" to folderName
        )

        db.collection("folders")
            .add(folderData)
            .addOnSuccessListener { docRef ->
                val newFolder = FolderItem( docRef.id, folderName, 0, docRef.id)
                val currentFolderList = folderListLiveData.value ?: emptyList()
                val updatedFolderList = (currentFolderList + newFolder)
                folderListLiveData.value = updatedFolderList
            }
        return 0;
    }

    fun editFolder(folderItem: FolderItem){
        val folderItemID = folderItem.getId()

        // Log the folder ID before proceeding with the update
        Log.d("EditFolder", "Editing folder with ID: $folderItemID")

        db.collection("folders").document(folderItem.getId())
            .update("name", folderItem.getName(), "numOfNotes", folderItem.getNumOfNotes(), "userID", folderItem.getUserID() )
            .addOnSuccessListener {
                val currentList = folderListLiveData.value ?: emptyList()
                val updatedList = currentList.toMutableList()

                val index = updatedList.indexOfFirst { it.getId() == folderItem.getId() }

                if (index != -1) {
                    updatedList[index] = folderItem
                    folderListLiveData.value = updatedList
                }
            }
    }

    fun deleteFolder(folderItem: FolderItem) {
        val folderItemID = folderItem.getId() // Assuming 'id' is the unique identifier for the folder in Firebase
        val folderCollectionRef = db.collection("folders")
        val notesCollectionRef = db.collection("notes")

        folderCollectionRef.document(folderItemID)
            .delete()
            .addOnSuccessListener {
                notesCollectionRef
                    .whereEqualTo("folderID", folderItemID)
                    .get()
                    .addOnSuccessListener { querySnapshot ->
                        val batch = db.batch()

                        for (document in querySnapshot.documents) {
                            val noteID = document.id
                            val noteRef = notesCollectionRef.document(noteID)
                            batch.delete(noteRef)
                        }
                        batch.commit()
                    }
                    .addOnFailureListener { exception ->
                    }

                val currentList = (folderListLiveData as MutableLiveData).value ?: emptyList()
                val updatedList = currentList.toMutableList()

                // Find the index of the folder item to remove
                val index = updatedList.indexOfFirst { it is FolderItem && it.getId() == folderItemID }

                if (index != -1) {
                    updatedList.removeAt(index)
                    (folderListLiveData as MutableLiveData).value = updatedList
                }
            }
            .addOnFailureListener { exception ->
            }
    }

//    fun deleteNote(noteItem: NotesItem.NoteDetail){
//        val noteID = noteItem.noteID
//
//        db.collection("notes").document(noteID)
//            .delete()
//            .addOnSuccessListener {
//                // Task has been deleted successfully from Firebase
//                val currentList = (noteListLiveData as MutableLiveData).value ?: emptyList()
//                val updatedList = currentList.toMutableList()
//
//                // Find the index of the item to remove
//                val index = updatedList.indexOfFirst { it is NotesItem.NoteDetail && it.noteID == noteID}
//
//                if (index != -1) {
//                    updatedList.removeAt(index)
//                    (noteListLiveData as MutableLiveData).value = updatedList
//                }
//            }
//    }
    fun deleteNote(noteItem: NotesItem.NoteDetail) {
        val noteID = noteItem.noteID

        db.collection("notes").document(noteID)
            .delete()
            .addOnSuccessListener {
                // Task has been deleted successfully from Firebase
                val currentList = (noteListLiveData as MutableLiveData).value ?: emptyList()
                val updatedList = currentList.toMutableList()

                // Find the index of the item to remove
                val index = updatedList.indexOfFirst { it is NotesItem.NoteDetail && it.noteID == noteID }

                if (index != -1) {
                    updatedList.removeAt(index)
                    (noteListLiveData as MutableLiveData).value = updatedList

                    // Check if the deleted item was the last detail under a header
                    val headerPosition = findHeaderPositionForDetail(index, updatedList)
                    if (headerPosition != -1 && isLastDetailUnderHeader(headerPosition, updatedList)) {
                        updatedList.removeAt(headerPosition)
                        (noteListLiveData as MutableLiveData).value = updatedList
                    }
                }
            }
    }
    private fun findHeaderPositionForDetail(detailPosition: Int, noteList: List<Any>): Int {
        for (i in minOf(detailPosition, noteList.size - 1) downTo 0) {
            val item = noteList[i]
            if (item is NotesItem.NoteHeader) {
                return i
            }
        }
        return -1
    }


    private fun isLastDetailUnderHeader(headerPosition: Int, noteList: List<Any>): Boolean {
        val header = noteList[headerPosition] as? NotesItem.NoteHeader
        if (header != null) {
            for (i in headerPosition + 1 until noteList.size) {
                val item = noteList[i]
                if (item is NotesItem.NoteDetail) {
                    return false
                }
            }
            return true
        }
        return false
    }

    fun insertNote(folderID: String, folderName: String, noteName: String, noteContent: String): Int{
        val noteData: Map<String, Any> = hashMapOf(
            "folderID" to folderID,
            "noteName" to noteName,
            "noteContent" to noteContent,
            "noteCreatedDate" to Timestamp.now()
        )

        db.collection("notes")
            .add(noteData)
            .addOnSuccessListener { docRef ->
                val newNote = NotesItem.NoteDetail(docRef.id, noteName, noteContent, folderName, Timestamp.now())
                val currentNoteList = noteListLiveData.value ?: emptyList()
                val updateNoteList = (currentNoteList + newNote)
                noteListLiveData.value = updateNoteList
            }

        val docRef = db.collection("folders").document(folderID)
        val fieldToIncrement = "numOfNotes"
        val incrementBy = 1

        val updates = hashMapOf(
            fieldToIncrement to FieldValue.increment(incrementBy.toLong())
        )

// Update the document with the increment operation
        docRef.update(updates as Map<String, Any>)
            .addOnSuccessListener {
                // The increment operation was successful
            }
            .addOnFailureListener { e ->
                // Handle any errors that occurred during the increment operation
            }

        return 0;

    }

    fun updateNote(noteID: String, folderID: String, folderName: String, noteName: String, noteContent: String, noteDateCreated: String) {
        val inputString = "Timestamp(seconds=1698638671, nanoseconds=703000000)"
        val secondsStart = inputString.indexOf("seconds=") + "seconds=".length
        val nanosecondsStart = inputString.indexOf("nanoseconds=") + "nanoseconds=".length
        val secondsSubstring = inputString.substring(secondsStart, inputString.indexOf(",", secondsStart))
        val nanosecondsSubstring = inputString.substring(nanosecondsStart, inputString.indexOf(")", nanosecondsStart))
        val secondsClean = secondsSubstring.filter { it.isDigit() }
        val nanosecondsClean = nanosecondsSubstring.filter { it.isDigit() }
        val seconds = secondsClean.toLong()
        val nanoseconds = nanosecondsClean.toInt()
        val timestamp = Timestamp(seconds, nanoseconds)

        val noteData: Map<String, Any> = hashMapOf(
            "folderID" to folderID,
            "noteName" to noteName,
            "noteContent" to noteContent,
            "noteCreatedDate" to timestamp
        )

        db.collection("notes")
            .document(noteID)
            .set(noteData)
            .addOnSuccessListener {
                val updatedNote = NotesItem.NoteDetail(noteID, noteName, noteContent, folderName, timestamp)
                val currentNoteList = noteListLiveData.value ?: emptyList()
                val updatedNoteList = currentNoteList.map { note ->
                    if (note is NotesItem.NoteDetail && note.noteID == noteID) {
                        updatedNote
                    } else {
                        note
                    }
                }
                noteListLiveData.value = updatedNoteList
            }
            .addOnFailureListener { exception ->
                println("Error updating note: $exception")
            }
    }
}