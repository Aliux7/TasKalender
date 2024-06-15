package edu.bluejack23_1.taskalender

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CalendarView
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Timestamp
import edu.bluejack23_1.taskalender.model.NotesItem
import edu.bluejack23_1.taskalender.view_model.note.NoteSelectListener
import edu.bluejack23_1.taskalender.view_model.note.NoteViewModel
import edu.bluejack23_1.taskalender.view_model.note.NotesListAdapter
import edu.bluejack23_1.taskalender.view_model.task.TaskViewModel
import edu.bluejack23_1.taskalender.view_model.task.TodoListAdapter
import java.text.SimpleDateFormat
import java.util.*

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class HomeFragment : Fragment(), NoteSelectListener {
    private var currentTimestamp: Timestamp = Timestamp.now()
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewModel: TaskViewModel
    private lateinit var adapter: TodoListAdapter
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var noteVM: NoteViewModel
    private lateinit var recyclerViewNote: RecyclerView
    private lateinit var adapterNote: NotesListAdapter

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
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        val sharedPreferences = requireContext().getSharedPreferences(LoginActivity.PREFS_NAME, Context.MODE_PRIVATE)
        val uid = sharedPreferences.getString(LoginActivity.UID, null)
        val calendarView = root.findViewById<CalendarView>(R.id.calendarView)
        val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.US)

        viewModel = ViewModelProvider(this)[TaskViewModel::class.java]
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)

        val selectedDate = formatDate(year, month, dayOfMonth)
        viewModel.loadTasksByDate(selectedDate, uid.toString())
        calendarView.setOnDateChangeListener { view, year, month, dayOfMonth ->
            val selectedDate = formatDate(year, month, dayOfMonth)
            viewModel.loadTasksByDate(selectedDate, uid.toString())
        }

        recyclerView = root.findViewById(R.id.taskRecyclerView)
        recyclerViewNote = root.findViewById(R.id.noteRecyclerView)

        val noTodoListText = root.findViewById<TextView>(R.id.noTodoListText)
        viewModel.getTaskListLiveData().observe(viewLifecycleOwner) { todoList ->
            val todoListArrayList = ArrayList(todoList)
            adapter = TodoListAdapter(todoListArrayList, viewModel)
            recyclerView.adapter = adapter
            recyclerView.layoutManager = LinearLayoutManager(requireContext())

            if (todoList.isEmpty()) {
                recyclerView.visibility = View.GONE
                noTodoListText.visibility = View.VISIBLE
            } else {
                recyclerView.visibility = View.VISIBLE
                noTodoListText.visibility = View.GONE
            }
        }

        noteVM = ViewModelProvider(this)[NoteViewModel::class.java]
        noteVM.loadAndSortNotesByUID(uid.toString()) //Userid

        noteVM.getNoteListLiveData().observe(viewLifecycleOwner) { noteList ->
            if (noteList.isNotEmpty()) {
                println("Testing $noteList")
                adapterNote = NotesListAdapter(noteList as ArrayList<Any>, this)
                recyclerViewNote.adapter = adapterNote
                recyclerViewNote.layoutManager = LinearLayoutManager(requireContext())

            }
        }
        return root
    }

    private fun formatDate(year: Int, month: Int, day: Int): String {
        return "$year-${month + 1}-$day"
    }

    override fun onItemClicked(noteItem: NotesItem.NoteDetail) {
//        val fragment = NotesFragment.newInstance("params1", "params2")
//        val transaction = parentFragmentManager.beginTransaction()
//        transaction.replace(R.id.HomeFragment, fragment)
//        transaction.addToBackStack(null)
//        transaction.commit()
    }

}