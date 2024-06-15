package edu.bluejack23_1.taskalender.view_model.task

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import edu.bluejack23_1.taskalender.model.TodoListItem
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class TaskViewModel : ViewModel() {

    private val taskListLiveData: MutableLiveData<List<TodoListItem>> = MutableLiveData()
    private val menuListLiveData: MutableLiveData<List<String>> = MutableLiveData()
    private val db = FirebaseFirestore.getInstance()

    fun loadAndSortTasks(menu: String, uid: String) {
        var tasksRef = db.collection("tasks")
            .whereEqualTo("userID", uid)

        if (menu != "All") {
            tasksRef = tasksRef.whereEqualTo("menu", menu)
        }

        tasksRef.get()
            .addOnSuccessListener { querySnapshot ->
                val taskList = mutableListOf<TodoListItem>()
                for (document in querySnapshot.documents) {
                    val task = document.toObject(TodoListItem::class.java)
                    println("Title: ${task?.title} ${task?.deadline}")
                    val id = document.id // Retrieve the document ID
                    if (task != null) {
                        task.id = id // Set the ID field in the task
                        taskList.add(task)
                    }
                }

                val sortedTaskList = taskList.sortedWith(compareBy<TodoListItem> { it.deadline }).sortedWith(compareBy<TodoListItem> { it.status })

                taskListLiveData.value = sortedTaskList
            }
    }


    fun loadTasksByDate(deadline: String, uid: String) {
        val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val selectedDate = dateFormatter.parse(deadline)

        db.collection("tasks")
            .whereEqualTo("userID", uid)
            .whereEqualTo("status", false)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val taskList = mutableListOf<TodoListItem>()
                for (document in querySnapshot.documents) {
                    println("ID: ${document.id}")
                    val task = document.toObject(TodoListItem::class.java)
                    val id = document.id
                    if (task != null) {
                        if (dateFormatter.format(task.deadline.toDate()) == dateFormatter.format(selectedDate)) {
                            task.id = id
                            taskList.add(task)
                        }
                    }
                }
                taskListLiveData.value = taskList
            }
    }

    fun editTask(task: TodoListItem) {
        db.collection("tasks").document(task.id)
            .update("title", task.title, "deadline", task.deadline, "menu", task.menu)
            .addOnSuccessListener {
                val currentList = taskListLiveData.value ?: emptyList()
                val updatedList = currentList.toMutableList()

                val index = updatedList.indexOfFirst { it.id == task.id }

                if (index != -1) {
                    updatedList[index] = task
                    taskListLiveData.value = updatedList
                }
            }
    }

    fun updateTaskStatus(task: TodoListItem) {
        db.collection("tasks").document(task.id)
            .update("status", task.status)
    }


    fun insertTask(task: TodoListItem) {
        db.collection("tasks")
            .add(task)
            .addOnSuccessListener {
                val newTask = task.copy()
                val currentTaskList = taskListLiveData.value ?: emptyList()
                val updatedTaskList = (currentTaskList + newTask).sortedWith(compareBy<TodoListItem> { it.deadline }).sortedWith(compareBy<TodoListItem> { it.status })
                taskListLiveData.value = updatedTaskList
            }
    }


    fun deleteTask(todoItem: TodoListItem) {
        val todoItemID = todoItem.id // Assuming 'id' is the unique identifier for the task in Firebase
        db.collection("tasks").document(todoItemID)
            .delete()
            .addOnSuccessListener {
                // Task has been deleted successfully from Firebase
                val currentList = (taskListLiveData as MutableLiveData).value ?: emptyList()
                val updatedList = currentList.toMutableList()

                // Find the index of the item to remove
                val index = updatedList.indexOfFirst { it is TodoListItem && it.id == todoItemID }

                if (index != -1) {
                    updatedList.removeAt(index)
                    (taskListLiveData as MutableLiveData).value = updatedList
                }
            }
    }


    fun getTaskListLiveData(): LiveData<List<TodoListItem>> {
        return taskListLiveData
    }

    fun fetchMenus(uid: String) {
        val menus = ArrayList<String>()

        db.collection("menus")
            .whereIn("access", listOf("Public", uid))
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val menu = document.getString("name")
                    menu?.let { menus.add(it) }
                }
                menuListLiveData.value = menus
            }
    }

    fun getMenuListLiveData(): LiveData<List<String>> {
        return menuListLiveData
    }

}