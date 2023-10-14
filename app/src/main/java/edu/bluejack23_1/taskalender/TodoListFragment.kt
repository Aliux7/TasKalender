package edu.bluejack23_1.taskalender

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.darwindeveloper.horizontalscrollmenulibrary.custom_views.HorizontalScrollMenuView
import com.darwindeveloper.horizontalscrollmenulibrary.custom_views.HorizontalScrollMenuView.OnHSMenuClickListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.twothreeone.taskalender.R
import com.twothreeone.taskalender.databinding.FragmentCashFlowBinding
import com.twothreeone.taskalender.databinding.FragmentTodoListBinding


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
private var binding: FragmentTodoListBinding? = null

class TodoListFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    private var binding: FragmentCashFlowBinding? = null
    private lateinit var recyclerView: RecyclerView

    private lateinit var horizontalScrollView: HorizontalScrollMenuView
    private lateinit var textView: TextView
    private lateinit var addTaskBtn: Button
    private var isPopupVisible = false
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

    }

    private fun initMenu() {
        if (::horizontalScrollView.isInitialized) {
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
        dialog.setContentView(R.layout.layout_add_task_dialog)
        val window = dialog.window
        val params = window?.attributes
        params?.width = ViewGroup.LayoutParams.MATCH_PARENT
        window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
// Set the height of the dialog to wrap content
        params?.height = ViewGroup.LayoutParams.WRAP_CONTENT

        window?.attributes = params
        window?.attributes = params

        dialog.window?.setBackgroundDrawableResource(R.color.background_color)

        val taskEditText = dialog.findViewById<EditText>(R.id.addTaskTxt)
        taskEditText.requestFocus()
        val inputMethodManager = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.toggleSoftInput(0, InputMethodManager.SHOW_IMPLICIT)

        val submitTaskButton = dialog.findViewById<Button>(R.id.submitTaskBtn)

        // Show the keyboard
        taskEditText.post {
            val inputMethodManager =
                requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.showSoftInput(taskEditText, InputMethodManager.SHOW_IMPLICIT)
        }

        submitTaskButton.setOnClickListener {
            val taskText = taskEditText.text.toString()
            dialog.dismiss()
            addDataToFirestore(taskEditText)
        }

        dialog.setOnDismissListener {
            isPopupVisible = false
            showAddTaskButton()
        }
        isPopupVisible = true

        dialog.show()
    }

    private fun hideAddTaskButton() {
        // Apply the fade-out animation to the button
        val fadeOut = AnimationUtils.loadAnimation(requireContext(), R.anim.fade_out)
        fadeOut.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {}
            override fun onAnimationRepeat(animation: Animation) {}

            override fun onAnimationEnd(animation: Animation) {
                // Hide the button when the fade-out animation ends
                addTaskBtn.visibility = View.INVISIBLE
            }
        })
        addTaskBtn.startAnimation(fadeOut)
    }

    private fun showAddTaskButton() {
        // Apply the fade-in animation to the button
        val fadeIn = AnimationUtils.loadAnimation(requireContext(), R.anim.fade_in)
        addTaskBtn.startAnimation(fadeIn)

        addTaskBtn.visibility = View.VISIBLE
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
            if (!isPopupVisible) {
                hideAddTaskButton()
                showAddTaskPopup()
            }
        }

        //make recycler view
        val taskItems = listOf(
            TaskItem("Make TaskKalendar", "2023-10-28"),
            TaskItem("Make TaskKalendar", "2023-10-28"),
            TaskItem("Make TaskKalendar", "2023-10-28"),
            TaskItem("Make TaskKalendar", "2023-10-28"),
            TaskItem("Make TaskKalendar", "2023-10-28"),
            TaskItem("Make TaskKalendar", "2023-10-28")
        )
        recyclerView = rootView.findViewById(R.id.taskRecyclerView)
        val adapter = TaskAdapter(taskItems)

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // You can now access horizontalScrollView
        return rootView
    }

    private fun addDataToFirestore(taskText: EditText) {
        val taskTextValue = taskText.text.toString()

        // Define the data to be added
        val data = hashMapOf(
            "name" to taskTextValue
        )

        val collectionReference = db.collection("tasks")
        collectionReference
            .add(data)
            .addOnSuccessListener { documentReference ->
                val autoGeneratedId = documentReference.id
                showToast("Task added")
            }
            .addOnFailureListener { e ->
                showToast("Failed to add task: ${e.message}")
            }
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}