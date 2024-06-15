package edu.bluejack23_1.taskalender.view_model.task

import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class LongPressHandler(
    private val recyclerView: RecyclerView,
    private val onItemLongClickListener: OnItemLongClickListener
) : RecyclerView.OnItemTouchListener {

    private val gestureDetector = GestureDetector(
        recyclerView.context,
        object : GestureDetector.SimpleOnGestureListener() {
            override fun onLongPress(e: MotionEvent) {
                val child = recyclerView.findChildViewUnder(e.x, e.y)
                if (child != null) {
                    val position = recyclerView.getChildAdapterPosition(child)
                    onItemLongClickListener.onItemLongClick(child, position)
                }
            }
        }
    )

    interface OnItemLongClickListener {
        fun onItemLongClick(view: View, position: Int)
    }

    override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
        gestureDetector.onTouchEvent(e)
        return false
    }

    override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {
        // Do nothing
    }

    override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {
        // Do nothing
    }
}
