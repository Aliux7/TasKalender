package edu.bluejack23_1.taskalender

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import edu.bluejack23_1.taskalender.TransactionItem
import com.twothreeone.taskalender.R


class TaskAdapter(private val items: List<TaskItem>):
    RecyclerView.Adapter<TaskAdapter.TaskViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_task_lists, parent, false)
        return TaskViewHolder(view)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val item = items[position]

        // Bind data to the views in your item layout
        holder.taskNameTextView.text = item.getName()
        holder.taskDeadlineTextView.text = item.getDeadline()
    }

    inner class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val taskNameTextView: TextView = itemView.findViewById(R.id.TaskName)
        val taskDeadlineTextView: TextView = itemView.findViewById(R.id.TaskDeadline)
    }
    }


