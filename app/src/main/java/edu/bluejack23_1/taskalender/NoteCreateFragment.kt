package edu.bluejack23_1.taskalender

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.Timestamp
import edu.bluejack23_1.taskalender.view_model.note.NoteViewModel
import java.text.SimpleDateFormat

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [NoteCreateFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class NoteCreateFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var folderId: String? = null
    private var folderName: String? = null
    private var noteName: String? = null
    private var noteContent: String? = null
    private var noteID: String? = null
    private var noteCreatedDate: String? = null


    private lateinit var submitBtn: Button
    private lateinit var cancelBtn: ImageView
    private lateinit var noteVM: NoteViewModel
    private lateinit var titleTxt: EditText
    private lateinit var contentTxt: EditText

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
        val rootView = inflater.inflate(R.layout.fragment_note_create, container, false)

        val sharedPreferences = requireContext().getSharedPreferences(
            LoginActivity.PREFS_NAME,
            Context.MODE_PRIVATE
        )

        val uid = sharedPreferences.getString(LoginActivity.UID, null)
        folderId = arguments?.getString(NoteCreateFragment.ARG_FOLDER_ID)
        folderName = arguments?.getString(NoteCreateFragment.ARG_FOLDER_NAME)
        noteName = arguments?.getString(NoteCreateFragment.ARG_NOTE_NAME)
        noteContent = arguments?.getString(NoteCreateFragment.ARG_NOTE_CONTENT)
        noteID = arguments?.getString(NoteCreateFragment.ARG_NOTE_ID)
        noteCreatedDate= arguments?.getString(NoteCreateFragment.ARG_NOTE_CREATED_DATE)

        var isUpdate: Boolean = false


        submitBtn = rootView.findViewById(R.id.submitNoteBtn)
        cancelBtn = rootView.findViewById(R.id.backToNotesBtn)
        titleTxt = rootView.findViewById(R.id.noteTitleTxt)
        contentTxt = rootView.findViewById(R.id.noteContentTxt)
        noteVM = ViewModelProvider(this)[NoteViewModel::class.java]

        if(noteName != null && noteContent!= null){
            titleTxt.setText(noteName)
            contentTxt.setText(noteContent)
            isUpdate = true
        }
        else{
            contentTxt.requestFocus()
            val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)
        }

        submitBtn.setOnClickListener{
            val noteName = titleTxt.text.toString()
            val noteContent = contentTxt.text.toString()

            if(isUpdate){
                if (folderId!= null && folderName!= null && folderName != null && folderId != null && noteID != null && noteCreatedDate!= null) {
                        noteVM.updateNote(noteID!!, folderId!!, folderName!!, noteName, noteContent,
                            noteCreatedDate!!
                        )
                        Toast.makeText(requireContext(), "Note Updated", Toast.LENGTH_SHORT).show()
                        moveToNoteListFragment()
                }
            }
            else{
                if (folderId!= null && folderName!= null ) {
                    noteVM.insertNote(
                        folderId!!, folderName!!, noteName, noteContent

                    )
                    Toast.makeText(requireContext(), "Note Added", Toast.LENGTH_SHORT).show()
                    moveToNoteListFragment()
                }
            }
        }
        cancelBtn.setOnClickListener{
            moveToNoteListFragment()
        }

        return rootView
    }

    fun moveToNoteListFragment(){
        if(folderId != null && folderName != null){
            val fragment = NotesListFragment.newInstance(folderId!!, folderName!!)
            val transaction = parentFragmentManager.beginTransaction()
            transaction.replace(R.id.noteCreateFrameLayout, fragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }
    }

    companion object {
        private const val ARG_FOLDER_ID = "folderId"
        private const val ARG_FOLDER_NAME = "folderName"
        private const val ARG_NOTE_NAME = "noteName"
        private const val ARG_NOTE_CONTENT = "noteContent"
        private const val ARG_NOTE_ID = "noteID"
        private const val ARG_NOTE_CREATED_DATE = "noteCreatedDate"



        @JvmStatic
        fun newInstance(folderId: String?, folderName: String?, noteName: String?, noteContent: String?, noteID: String?, noteCreatedDate:String?): NoteCreateFragment {
            val fragment = NoteCreateFragment()
            val args = Bundle()
            args.putString(NoteCreateFragment.ARG_FOLDER_ID, folderId)
            args.putString(NoteCreateFragment.ARG_FOLDER_NAME, folderName)
            args.putString(NoteCreateFragment.ARG_NOTE_NAME, noteName)
            args.putString(NoteCreateFragment.ARG_NOTE_CONTENT, noteContent)
            args.putString(NoteCreateFragment.ARG_NOTE_ID, noteID)
            args.putString(NoteCreateFragment.ARG_NOTE_CREATED_DATE, noteCreatedDate)

            fragment.arguments = args
            return fragment
        }
    }
}