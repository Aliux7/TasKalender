package edu.bluejack23_1.taskalender.view_model.cash

import android.content.Context
import android.graphics.Canvas
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import edu.bluejack23_1.taskalender.R
import edu.bluejack23_1.taskalender.model.TransactionItem
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator

class SwipeToDeleteCallback(
    private val viewModel: CashViewModel,
    private val recyclerView: RecyclerView
) : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
    private val swipeDecorator = SwipeDecorator(recyclerView.context)

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false;
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        val position = viewHolder.adapterPosition
        val item = viewModel.getTransactionAtPosition(position)


        if (item is TransactionItem.TransactionDetail) {
            val headerPosition = findHeaderPositionForDetail(position)
            viewModel.deleteTransactionAndHeader(item, headerPosition)
        }
    }
    private fun findHeaderPositionForDetail(detailPosition: Int): Int {
        for (i in detailPosition downTo 0) {
            val item = viewModel.getTransactionAtPosition(i)
            if (item is TransactionItem.TransactionHeader) {
                return i
            }
        }
        return -1
    }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        swipeDecorator.decorate(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }
}

class SwipeDecorator(private val context: Context) {
    fun decorate(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
        RecyclerViewSwipeDecorator.Builder(context, c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            .addBackgroundColor(ContextCompat.getColor(context, R.color.delete_color))
            .addSwipeLeftBackgroundColor(ContextCompat.getColor(context, R.color.delete_color))
            .addSwipeLeftActionIcon(R.drawable.baseline_delete_24_white)
            .create()
            .decorate()
    }
}
