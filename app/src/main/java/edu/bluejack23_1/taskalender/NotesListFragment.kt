package edu.bluejack23_1.taskalender

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import edu.bluejack23_1.taskalender.model.NotesItem
import edu.bluejack23_1.taskalender.view_model.note.NoteSelectListener
import edu.bluejack23_1.taskalender.view_model.note.NoteViewModel
import edu.bluejack23_1.taskalender.view_model.note.NotesListAdapter
import edu.bluejack23_1.taskalender.view_model.task.LongPressHandler
import java.util.ArrayList

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class NotesListFragment : Fragment(), NoteSelectListener {

    private var param1: String? = null
    private var param2: String? = null

    private lateinit var noteVM: NoteViewModel

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: NotesListAdapter
    private var folderId: String? = null
    private var folderName: String? = null

    private lateinit var addNoteBtn: FloatingActionButton
    private lateinit var backToFolderBtn: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_notes_list, container, false)
        recyclerView = rootView.findViewById(R.id.noteRecyclerView)

        val sharedPreferences = requireContext().getSharedPreferences(
            LoginActivity.PREFS_NAME,
            Context.MODE_PRIVATE
        )

        val uid = sharedPreferences.getString(LoginActivity.UID, null)
        folderId = arguments?.getString(ARG_FOLDER_ID)
        folderName = arguments?.getString(ARG_FOLDER_NAME)

        if (folderId != null) {
            noteVM = ViewModelProvider(this)[NoteViewModel::class.java]
            noteVM.loadAndSortNotes(folderId!!)
        }
        

        noteVM.getNoteListLiveData().observe(viewLifecycleOwner) { noteList ->
            if (noteList.isNotEmpty()) {
                adapter = NotesListAdapter(noteList as ArrayList<Any>, this)
                recyclerView.adapter = adapter
                recyclerView.layoutManager = LinearLayoutManager(requireContext())

                }
            }
        addNoteBtn = rootView.findViewById(R.id.addNoteBtn)
        addNoteBtn.setOnClickListener{
//            val intent = Intent(requireActivity(), NoteCreateActivity::class.java)
//            intent.putExtra("folderId", folderId)
//            intent.putExtra("folderName", folderName)
//
//            startActivity(intent)
            Log.d("createnote", "fetch notes with folder with ID: $folderId")

            val fragment = NoteCreateFragment.newInstance(folderId, folderName, null, null, null, null)
            val transaction = parentFragmentManager.beginTransaction()
            transaction.replace(R.id.notesListFrameLayout, fragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }

        backToFolderBtn = rootView.findViewById(R.id.backToFolderBtn)
        backToFolderBtn.setOnClickListener {
            val fragment = NotesFragment.newInstance("param1", "param2")
            val transaction = parentFragmentManager.beginTransaction()
            transaction.replace(R.id.notesListFrameLayout, fragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }

        val longPressHandler = LongPressHandler(recyclerView, object : LongPressHandler.OnItemLongClickListener {
            override fun onItemLongClick(view: View, position: Int) {
                println("View: $view Position: $position")
                deleteNoteDialog(position)
            }
        })

        recyclerView.addOnItemTouchListener(longPressHandler)

        return rootView
    }

    fun deleteNoteDialog(position: Int){
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.note_delete_dialog)

        val note = noteVM.getNoteListLiveData().value?.get(position)
        val deleteBtn = dialog.findViewById<TextView>(R.id.deleteBtn)

        deleteBtn.setOnClickListener{
            if(note != null){
                noteVM.deleteNote(note as NotesItem.NoteDetail)

                Toast.makeText(requireContext(), "Note Deleted Successful", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }
        }
        dialog.show()
        val window = dialog.window
        window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        window?.attributes?.windowAnimations = R.style.DialogAnimation
        window?.setGravity(Gravity.BOTTOM)

    }

    companion object {
        private const val ARG_FOLDER_ID = "folderId"
        private const val ARG_FOLDER_NAME = "folderName"

        fun newInstance(folderId: String, folderName: String): NotesListFragment {
            val fragment = NotesListFragment()
            val args = Bundle()
            Log.d("folderInList", "fetch notes with folder with ID: $folderId")


            args.putString(ARG_FOLDER_ID, folderId)
            args.putString(ARG_FOLDER_NAME, folderName)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onItemClicked(noteItem: NotesItem.NoteDetail) {
        val fragment = NoteCreateFragment.newInstance(folderId, folderName, noteItem.noteName, noteItem.noteContent, noteItem.noteID, noteItem.noteCreatedDate.toString())
        val transaction = parentFragmentManager.beginTransaction()
        transaction.replace(R.id.notesListFrameLayout, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }
}