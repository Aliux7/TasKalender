package edu.bluejack23_1.taskalender

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import edu.bluejack23_1.taskalender.model.FolderItem
import edu.bluejack23_1.taskalender.view_model.note.FolderListAdapter
import edu.bluejack23_1.taskalender.view_model.note.FolderSelectListener
import edu.bluejack23_1.taskalender.view_model.note.NoteViewModel
import edu.bluejack23_1.taskalender.view_model.task.LongPressHandler
import edu.bluejack23_1.taskalender.view_model.task.TaskViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [NotesFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class NotesFragment : Fragment(), FolderSelectListener {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var noteVM: NoteViewModel
    private lateinit var folderAdapter: FolderListAdapter
    private lateinit var folderRecyclerView: RecyclerView
    private lateinit var addFolderBtn: FloatingActionButton
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }



    override fun onItemClicked(folderItem: FolderItem) {
        Log.d("folderInParent", "fetch notes with folder with ID: $folderItem.getId()")


        val fragment = NotesListFragment.newInstance(folderItem.getId(), folderItem.getName())
        val transaction = parentFragmentManager.beginTransaction()
        transaction.replace(R.id.notesFrameLayout, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_notes, container, false)
        val sharedPreferences = requireContext().getSharedPreferences(LoginActivity.PREFS_NAME,Context.MODE_PRIVATE)
        val uid = sharedPreferences.getString(LoginActivity.UID, null)

        noteVM = ViewModelProvider(this)[NoteViewModel::class.java]
        noteVM.loadAllFolders(uid.toString())

        folderRecyclerView = rootView.findViewById(R.id.folderRecyclerView)
        val layoutManager = GridLayoutManager(requireContext(), 2)
        folderRecyclerView.layoutManager = layoutManager
        noteVM.getFolderListLiveData().observe(viewLifecycleOwner){ folderList ->
            val folderArrayList = ArrayList(folderList)
            folderAdapter = FolderListAdapter(folderArrayList, noteVM, this)
            folderRecyclerView.adapter = folderAdapter
        }

        //add new folder
        addFolderBtn = rootView.findViewById(R.id.addFolderBtn)
        addFolderBtn.setOnClickListener{
            showAddFolderDialog()
        }

        val longPressHandler = LongPressHandler(folderRecyclerView, object : LongPressHandler.OnItemLongClickListener {
            override fun onItemLongClick(view: View, position: Int) {
                println("View: $view Position: $position")
                editFolderDialog(position)
            }
        })

        folderRecyclerView.addOnItemTouchListener(longPressHandler)

        return rootView
    }

    @SuppressLint("SuspiciousIndentation")
    private fun editFolderDialog(position: Int) {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.folder_edit_dialog)

        val sharedPreferences = requireContext().getSharedPreferences(
            LoginActivity.PREFS_NAME,
            Context.MODE_PRIVATE
        )
        val uid = sharedPreferences.getString(LoginActivity.UID, null)

        val folder = noteVM.getFolderListLiveData().value?.get(position)

        val titleTextInputLayout = dialog.findViewById<TextInputLayout>(R.id.nameContainer)
        val titleTextInput = dialog.findViewById<TextInputEditText>(R.id.folderName)
        titleTextInput.setText(folder?.getName())
        val editButton = dialog.findViewById<Button>(R.id.editBtn)
        val deleteTaskTW = dialog.findViewById<TextView>(R.id.deleteBtn)

        editButton.setOnClickListener {
            // Validate and get input data
            val title = titleTextInput.text.toString()

            val sharedPreferences = requireContext().getSharedPreferences(
                LoginActivity.PREFS_NAME,
                Context.MODE_PRIVATE
            )
            val uid = sharedPreferences.getString(LoginActivity.UID, null)

            if (title.isNotEmpty()) {
                if (folder != null) {
                    val updatedFolder = folder.copy(id = folder.getId(), name = title.toString(), numOfNotes = folder.getNumOfNotes(), userID = folder.getUserID())
                        noteVM.editFolder(updatedFolder)
                        Toast.makeText(requireContext(), "Edit Folder Successful", Toast.LENGTH_SHORT).show()

                    dialog.dismiss()
                }
            } else {
                titleTextInputLayout?.error = "Title is required"
            }
        }

        deleteTaskTW.setOnClickListener{
            if(folder != null){
                noteVM.deleteFolder(folder)

                Toast.makeText(requireContext(), "Folder Deleted Successful", Toast.LENGTH_SHORT).show()
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

    private fun showAddFolderDialog() {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.folder_add_dialog)

        dialog.show()
        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.attributes?.windowAnimations = R.style.DialogAnimation
        dialog.window?.setGravity(Gravity.BOTTOM)

        val addFolderTxt = dialog.findViewById<EditText>(R.id.addFolderTxt)
        addFolderTxt.requestFocus()
        val inputMethodManager = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.toggleSoftInput(0, InputMethodManager.SHOW_IMPLICIT)
        addFolderTxt.post {
            val inputMethodManager =
                requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.showSoftInput(addFolderTxt, InputMethodManager.SHOW_IMPLICIT)
        }

        val submitFolderBtn = dialog.findViewById<Button>(R.id.submitFolderBtn)
        val cancelFolderBtn = dialog.findViewById<Button>(R.id.cancelSubmitFolderBtn)

        val sharedPreferences = requireContext().getSharedPreferences(
            LoginActivity.PREFS_NAME,
            Context.MODE_PRIVATE
        )
        val uid = sharedPreferences.getString(LoginActivity.UID, null)

        submitFolderBtn.setOnClickListener{
            val folderName = addFolderTxt.text.toString()
            dialog.dismiss()
            noteVM.insertFolder(folderName, uid.toString())
        }

        cancelFolderBtn.setOnClickListener {
            dialog.dismiss()
        }
    }



    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment NotesFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            NotesFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }


}