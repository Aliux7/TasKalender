package edu.bluejack23_1.taskalender

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.darwindeveloper.horizontalscrollmenulibrary.custom_views.HorizontalScrollMenuView
import com.darwindeveloper.horizontalscrollmenulibrary.custom_views.HorizontalScrollMenuView.OnHSMenuClickListener
import com.twothreeone.taskalender.R


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class TodoListFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var horizontalScrollView: HorizontalScrollMenuView
    private lateinit var textView: TextView
    private lateinit var addTaskBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    private fun initMenu() {
        if (::horizontalScrollView.isInitialized) {
            horizontalScrollView.addItem("Default", 0)
            horizontalScrollView.addItem("Home", 0)
            horizontalScrollView.addItem("Work", 0)
            horizontalScrollView.addItem("Add", 0)

            horizontalScrollView.setOnHSMenuClickListener(OnHSMenuClickListener { menuItem, position ->
                textView.text = menuItem.text
            })
        } else {
            Log.e("YourTag", "horizontalScrollView is not initialized, cannot add items")
        }
    }

    private fun showAddTaskPopup(){
        val dialog = Dialog(requireContext())
//        dialog.setContentView(R.layout.)

        val taskEditText = dialog.findViewById<EditText>(R.id.addTaskTxt)
        val submitTaskButton = dialog.findViewById<Button>(R.id.addTaskBtn)

        // Show the keyboard
        taskEditText.post {
            val inputMethodManager =
                requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.showSoftInput(taskEditText, InputMethodManager.SHOW_IMPLICIT)
        }

        submitTaskButton.setOnClickListener {
            val taskText = taskEditText.text.toString()
            // Handle the task text (e.g., save it, update UI, etc.)

            dialog.dismiss()
        }

        dialog.show()
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_todo_list, container, false)
        horizontalScrollView = rootView.findViewById(R.id.taskMenu)
        textView = rootView.findViewById((R.id.textView))
        addTaskBtn = rootView.findViewById(R.id.addTaskBtn)
        initMenu()
        addTaskBtn.setOnClickListener {
            showAddTaskPopup()
        }

        // You can now access horizontalScrollView
        return rootView
    }
}