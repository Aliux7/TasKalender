package edu.bluejack23_1.taskalender.view_model.task

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.RadioButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import edu.bluejack23_1.taskalender.R
import edu.bluejack23_1.taskalender.model.TodoListItem
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class TodoListAdapter(private val items: ArrayList<TodoListItem>,
                      private val viewModel: TaskViewModel) :
    RecyclerView.Adapter<TodoListAdapter.TodoItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val itemView = inflater.inflate(R.layout.todo_list_item_view, parent, false)
        return TodoItemViewHolder(itemView)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: TodoItemViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item)
    }

    inner class TodoItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val statusCheckBox = itemView.findViewById<CheckBox>(R.id.statusCheckBox)

        init {
            itemView.setOnClickListener {
                // Toggle the checkbox when any part of the item is clicked
                statusCheckBox.isChecked = !statusCheckBox.isChecked
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val task = items[position]
                    println("Task : $task.id ${task.status}")

                    task.status = statusCheckBox.isChecked
                    viewModel.updateTaskStatus(task)
                }
            }

        }

        fun bind(item: TodoListItem) {
            val titleTextView = itemView.findViewById<TextView>(R.id.titleTxt)
            val deadlineTextView = itemView.findViewById<TextView>(R.id.deadlineTxt)
            statusCheckBox.isChecked = item.status

            // Set other data as well (title, deadline, etc.)
            titleTextView.text = item.title
            val deadlineText = SimpleDateFormat("dd MMMM yyyy", Locale.US).format(item.deadline.toDate())
            deadlineTextView.text = "$deadlineText"
        }
    }

}
