package edu.bluejack23_1.taskalender

import android.app.*
import android.app.Notification
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.darwindeveloper.horizontalscrollmenulibrary.custom_views.HorizontalScrollMenuView
import com.darwindeveloper.horizontalscrollmenulibrary.custom_views.HorizontalScrollMenuView.OnHSMenuClickListener
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import edu.bluejack23_1.taskalender.model.TodoListItem
import edu.bluejack23_1.taskalender.view_model.task.LongPressHandler
import edu.bluejack23_1.taskalender.view_model.task.TaskViewModel
import edu.bluejack23_1.taskalender.view_model.task.TodoListAdapter
import java.text.SimpleDateFormat
import java.util.*
import android.app.NotificationManager
import android.content.Intent
import androidx.core.app.NotificationCompat
import edu.bluejack23_1.taskalender.view_model.*


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class TodoListFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    private var selectedMenu: String = "All"
    private lateinit var viewModel: TaskViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var horizontalScrollView: HorizontalScrollMenuView
    private var formatDate = SimpleDateFormat("dd MMMM YYYY", Locale.US)
    private var dateTextInputLayout: TextInputLayout? = null
    private var dateTextInput: TextView? = null
    private lateinit var textView: TextView
    private lateinit var addTaskBtn: FloatingActionButton
    private lateinit var adapter: TodoListAdapter
    private var isPopupVisible = false
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        createNotificationChannel()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_todo_list, container, false)
        val sharedPreferences = requireContext().getSharedPreferences(LoginActivity.PREFS_NAME,Context.MODE_PRIVATE)
        val uid = sharedPreferences.getString(LoginActivity.UID, null)

        viewModel = ViewModelProvider(this)[TaskViewModel::class.java]
        viewModel.loadAndSortTasks(selectedMenu, uid.toString())

        textView = rootView.findViewById(R.id.textView)
        horizontalScrollView = rootView.findViewById(R.id.taskMenu)
        recyclerView = rootView.findViewById(R.id.taskRecyclerView)
        addTaskBtn = rootView.findViewById(R.id.addTaskBtn)
        initMenu()
        viewModel.getTaskListLiveData().observe(viewLifecycleOwner) { todoList ->
            val todoListArrayList = ArrayList(todoList)
            adapter = TodoListAdapter(todoListArrayList, viewModel)
            recyclerView.adapter = adapter
            recyclerView.layoutManager = LinearLayoutManager(requireContext())

        }
        val longPressHandler = LongPressHandler(recyclerView, object : LongPressHandler.OnItemLongClickListener {
            override fun onItemLongClick(view: View, position: Int) {
                println("View: $view Position: $position")
                editTaskDialog(position)
            }
        })
        addTaskBtn.setOnClickListener{
            addTaskDialog()
        }

        recyclerView.addOnItemTouchListener(longPressHandler)

        return rootView
    }
    private fun initMenu() {
        if (::horizontalScrollView.isInitialized) {
            horizontalScrollView.addItem("All", 0)
            horizontalScrollView.addItem("Home", 0)
            horizontalScrollView.addItem("Work", 0)
            horizontalScrollView.addItem("Other", 0)

            horizontalScrollView.setOnHSMenuClickListener(OnHSMenuClickListener { menuItem, position ->
                textView.text = menuItem.text
                selectedMenu = menuItem.text
                val sharedPreferences = requireContext().getSharedPreferences(LoginActivity.PREFS_NAME,Context.MODE_PRIVATE)
                val uid = sharedPreferences.getString(LoginActivity.UID, null)
                viewModel.loadAndSortTasks(selectedMenu, uid.toString())
            })
        } else {
            Log.e("YourTag", "horizontalScrollView is not initialized, cannot add items")
        }
    }


    private fun addTaskDialog() {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.todo_list_add_dialog)

        val sharedPreferences = requireContext().getSharedPreferences(
            LoginActivity.PREFS_NAME,
            Context.MODE_PRIVATE
        )
        val uid = sharedPreferences.getString(LoginActivity.UID, null)

        dateTextInputLayout = dialog.findViewById(R.id.dateContainer)
        dateTextInput = dialog.findViewById(R.id.date)
        val today = Calendar.getInstance()
        dateTextInput?.text = formatDate.format(today.time)

        dateTextInputLayout?.setOnClickListener {
            val getDate = Calendar.getInstance()
            val datePicker = DatePickerDialog(
                requireContext(),
                android.R.style.Theme_Holo_Dialog,
                DatePickerDialog.OnDateSetListener { _, i, i2, i3 ->
                    val selectDate = Calendar.getInstance()
                    selectDate.set(Calendar.YEAR, i)
                    selectDate.set(Calendar.MONTH, i2)
                    selectDate.set(Calendar.DAY_OF_MONTH, i3)
                    val date = formatDate.format(selectDate.time)
                    dateTextInput?.text = date
                },
                getDate.get(Calendar.YEAR),
                getDate.get(Calendar.MONTH),
                getDate.get(Calendar.DAY_OF_MONTH)
            )
            datePicker.show();
        }

        val titleTextInputLayout = dialog.findViewById<TextInputLayout>(R.id.titleContainer)
        val titleTextInput = dialog.findViewById<TextInputEditText>(R.id.title)
        val addButton = dialog.findViewById<Button>(R.id.addButton)

        addButton.setOnClickListener {
            // Validate and get input data
            val title = titleTextInput.text.toString()

            val sharedPreferences = requireContext().getSharedPreferences(
                LoginActivity.PREFS_NAME,
                Context.MODE_PRIVATE
            )
            val uid = sharedPreferences.getString(LoginActivity.UID, null)

            val date = dateTextInput?.text.toString()
            val inputFormat = SimpleDateFormat("dd MMMM yyyy", Locale.US)
            val dateFormat = inputFormat.parse(date)
            val timestamp = dateFormat?.time


            val taskData: Map<String, Any> = hashMapOf(
                "userID" to uid.toString(),
                "deadline" to Timestamp(Date(timestamp ?: 0)),
                "menu" to selectedMenu,
                "status" to false,
                "title" to title.toString()
            )

            if (!date.equals("Date Transaction") && title.isNotEmpty()) {
                db.collection("tasks").add(taskData)
                    .addOnSuccessListener { documentReference ->
                        val newTaskID = documentReference.id
                        val newTask = TodoListItem(
                            newTaskID,
                            title,
                            Timestamp(Date(timestamp ?: 0)),
                            selectedMenu,
                            false
                        )

                        viewModel.insertTask(newTask)
                        scheduleNotification(taskData, dialog)
                        adapter.notifyDataSetChanged()
                    }
                Toast.makeText(requireContext(), "Add Task Successful", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            } else {
                if (date.equals("Date Transaction")) {
                    dateTextInputLayout?.error = "Date is required"
                }
                if(title.isEmpty()){
                    titleTextInputLayout?.error = "Title is required"
                }
            }
        }

        dialog.show()
        val window = dialog.window
        window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        window?.attributes?.windowAnimations = R.style.DialogAnimation
        window?.setGravity(Gravity.BOTTOM)
    }

    private fun editTaskDialog(position: Int) {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.todo_list_edit_dialog)

        val sharedPreferences = requireContext().getSharedPreferences(
            LoginActivity.PREFS_NAME,
            Context.MODE_PRIVATE
        )
        val uid = sharedPreferences.getString(LoginActivity.UID, null)

        val task = viewModel.getTaskListLiveData().value?.get(position)
        dateTextInputLayout = dialog.findViewById(R.id.dateContainer)
        dateTextInput = dialog.findViewById(R.id.date)

        val deadline = task?.deadline?.toDate()
        dateTextInput?.text = formatDate.format(deadline)

        dateTextInputLayout?.setOnClickListener {
            val getDate = Calendar.getInstance()
            val datePicker = DatePickerDialog(
                requireContext(),
                android.R.style.Theme_Holo_Dialog,
                DatePickerDialog.OnDateSetListener { _, i, i2, i3 ->
                    val selectDate = Calendar.getInstance()
                    selectDate.set(Calendar.YEAR, i)
                    selectDate.set(Calendar.MONTH, i2)
                    selectDate.set(Calendar.DAY_OF_MONTH, i3)
                    val date = formatDate.format(selectDate.time)
                    dateTextInput?.text = date
                },
                getDate.get(Calendar.YEAR),
                getDate.get(Calendar.MONTH),
                getDate.get(Calendar.DAY_OF_MONTH)
            )
            datePicker.show();
        }

        val titleTextInputLayout = dialog.findViewById<TextInputLayout>(R.id.titleContainer)
        val titleTextInput = dialog.findViewById<TextInputEditText>(R.id.title)
        titleTextInput.setText(task?.title)
        val editButton = dialog.findViewById<Button>(R.id.editButton)
        val deleteTaskTW = dialog.findViewById<TextView>(R.id.deleteTaskTW)

        editButton.setOnClickListener {
            // Validate and get input data
            val title = titleTextInput.text.toString()

            val sharedPreferences = requireContext().getSharedPreferences(
                LoginActivity.PREFS_NAME,
                Context.MODE_PRIVATE
            )
            val uid = sharedPreferences.getString(LoginActivity.UID, null)

            val date = dateTextInput?.text.toString()
            val inputFormat = SimpleDateFormat("dd MMMM yyyy", Locale.US)
            val dateFormat = inputFormat.parse(date)
            val timestamp = dateFormat?.time

            if (!date.equals("Date Transaction") && title.isNotEmpty()) {
                if (task != null) {
                    val updatedTask = task.copy(deadline = Timestamp(Date(timestamp ?: 0)), title = title.toString())
                    viewModel.editTask(updatedTask)

                    Toast.makeText(requireContext(), "Edit Task Successful", Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                }
            } else {
                if (date.equals("Date Transaction")) {
                    dateTextInputLayout?.error = "Date is required"
                }
                if(title.isEmpty()){
                    titleTextInputLayout?.error = "Title is required"
                }
            }
        }

        deleteTaskTW.setOnClickListener{
            if(task != null){
                viewModel.deleteTask(task)

                Toast.makeText(requireContext(), "Task Deleted Successful", Toast.LENGTH_SHORT).show()
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

    private fun scheduleNotification(taskData: Map<String, Any>, dialog: Dialog)
    {

        val intent = Intent(requireContext(), Notification::class.java)
        intent.action = "edu.bluejack23_1.taskalender.CUSTOM_ACTION"
        intent.putExtra(titleExtra, "Task Kalender")
        intent.putExtra(messageExtra,  taskData["title"] as String)

        val pendingIntent = PendingIntent.getBroadcast(
            requireContext(),
            notificationID,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )


//        val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
//        val time = getTime(taskData)
//        alarmManager.setExactAndAllowWhileIdle(
//            AlarmManager.RTC_WAKEUP,
//            time,
//            pendingIntent
//        )
//        showAlert(time, "Task Kalender", taskData["title"] as String)
        showNotification("New Todo List", taskData["title"] as String)
    }
    private fun showNotification(title: String, message: String) {
        val notificationManager = requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notification = NotificationCompat.Builder(requireContext(), channelID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
            .build()

        // NotificationID is a unique ID for the notification
        notificationManager.notify(notificationID, notification)
    }
    private fun showAlert(time: Long, title: String, message: String)
    {
        val date = Date(time)
        val dateFormat = android.text.format.DateFormat.getLongDateFormat(requireContext())
        val timeFormat = android.text.format.DateFormat.getTimeFormat(requireContext())

        AlertDialog.Builder(requireContext())
            .setTitle("Notification Scheduled")
            .setMessage(
                "Title: " + title +
                        "\nMessage: " + message +
                        "\nAt: " + dateFormat.format(date) + " " + timeFormat.format(date))
            .setPositiveButton("Okay"){_,_ ->}
            .show()
    }

    private fun getTime(taskData: Map<String, Any>): Long
    {
        val deadlineTimestamp = taskData["deadline"] as Timestamp

        val calendar = Calendar.getInstance()
        calendar.time = deadlineTimestamp.toDate()
        calendar.set(Calendar.HOUR_OF_DAY, 21)
        calendar.set(Calendar.MINUTE, 15)

        return calendar.timeInMillis
    }

    private fun createNotificationChannel()
     {
        val name = "Notif Channel"
        val desc = "A Description of the Channel"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(channelID, name, importance)
        channel.description = desc
        val notificationManager = requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}