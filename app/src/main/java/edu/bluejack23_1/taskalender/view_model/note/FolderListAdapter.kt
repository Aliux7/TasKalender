package edu.bluejack23_1.taskalender.view_model.note

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import edu.bluejack23_1.taskalender.R
import edu.bluejack23_1.taskalender.model.FolderItem


class FolderListAdapter(private val items: List<FolderItem>, private val viewModel: NoteViewModel, private val folderSelectListener: FolderSelectListener):
    RecyclerView.Adapter<FolderListAdapter.FolderViewHolder>(){


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FolderViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.folder_item_view, parent, false)
        return FolderViewHolder(view)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: FolderViewHolder, position: Int) {
        val item = items[position]

        // Bind data to the views in your item layout
        holder.folderNameTextView.text = item.getName()
        holder.numOfNotesTextView.text = item.getNumOfNotes().toString()
    }

    inner class FolderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val folderCardView: MaterialCardView = itemView.findViewById(R.id.folderCard)
        val folderNameTextView: TextView = itemView.findViewById(R.id.folderName)
        val numOfNotesTextView: TextView = itemView.findViewById(R.id.numberOfNotes)

        init {
            folderCardView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val folderItem = items[position]
                    folderSelectListener.onItemClicked(folderItem)
                }
            }
        }
    }
}