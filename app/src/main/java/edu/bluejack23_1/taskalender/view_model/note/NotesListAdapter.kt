package edu.bluejack23_1.taskalender.view_model.note

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import edu.bluejack23_1.taskalender.R
import edu.bluejack23_1.taskalender.model.NotesItem
import java.text.SimpleDateFormat
import java.util.Locale

class NotesListAdapter(private val items: ArrayList<Any>, private val noteSelectListener: NoteSelectListener) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val HEADER_VIEW_TYPE = 0
    private val NOTE_VIEW_TYPE = 1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            HEADER_VIEW_TYPE -> {
                val headerView = inflater.inflate(R.layout.note_header_view, parent, false)
                HeaderViewHolder(headerView)
            }
            NOTE_VIEW_TYPE -> {
                val noteView = inflater.inflate(R.layout.notes_item_view, parent, false)
                NoteViewHolder(noteView)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is NotesItem.NoteHeader -> HEADER_VIEW_TYPE
            is NotesItem.NoteDetail -> NOTE_VIEW_TYPE
            else -> -1
        }
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is NotesListAdapter.HeaderViewHolder -> {
                val headerItem = items[position] as NotesItem.NoteHeader
                holder.bind(headerItem)
            }
            is NotesListAdapter.NoteViewHolder -> {
                val noteItem = items[position] as NotesItem.NoteDetail
                holder.bind(noteItem)
            }
        }

        if (position > 0 && items[position] is NotesItem.NoteHeader) {
            val layoutParams = holder.itemView.layoutParams as RecyclerView.LayoutParams
            layoutParams.topMargin = 50 // Set the desired margin size
            holder.itemView.layoutParams = layoutParams
        }
    }

    inner class HeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(headerItem: NotesItem.NoteHeader) {
            // Bind the header data to the views in the header layout
            val headerTextView = itemView.findViewById<TextView>(R.id.noteHeader)
            headerTextView.text = SimpleDateFormat("MMMM", Locale.US).format(headerItem.date.toDate())
        }
    }

    inner class NoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(noteItem: NotesItem.NoteDetail) {
            // Bind the data to the views in the note layout
            val noteNameTextView = itemView.findViewById<TextView>(R.id.notesName)
            val noteDateTextView = itemView.findViewById<TextView>(R.id.notesDate)
            val notePreviewTextView = itemView.findViewById<TextView>(R.id.notesPreview)
            val noteFolderNameTextView = itemView.findViewById<TextView>(R.id.folderName)
            val noteCard: ConstraintLayout = itemView.findViewById(R.id.notesContainer)

            noteNameTextView.text = noteItem.noteName
            noteDateTextView.text = SimpleDateFormat("dd/MM/yyyy", Locale.US).format(noteItem.noteCreatedDate.toDate())
            notePreviewTextView.text = noteItem.noteContent
            noteFolderNameTextView.text = noteItem.noteFolderName

            noteCard.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val noteItem = items[position]
                    if (noteItem is NotesItem.NoteDetail) {
                        noteSelectListener.onItemClicked(noteItem)
                    }
                }
            }
        }
    }
}
