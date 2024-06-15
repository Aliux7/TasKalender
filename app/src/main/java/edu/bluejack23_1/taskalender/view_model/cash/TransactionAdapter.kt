package edu.bluejack23_1.taskalender.view_model.cash

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import edu.bluejack23_1.taskalender.R
import edu.bluejack23_1.taskalender.model.TransactionItem
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class TransactionAdapter(private val items: ArrayList<Any>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val HEADER_VIEW_TYPE = 0
    private val INCOME_VIEW_TYPE = 1
    private val EXPENSE_VIEW_TYPE = 2

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            HEADER_VIEW_TYPE -> {
                val headerView = inflater.inflate(R.layout.transaction_header_view, parent, false)
                HeaderViewHolder(headerView)
            }
            INCOME_VIEW_TYPE -> {
                val incomeView = inflater.inflate(R.layout.income_transaction_view, parent, false)
                IncomeViewHolder(incomeView)
            }
            EXPENSE_VIEW_TYPE -> {
                val expenseView = inflater.inflate(R.layout.outcome_transaction_view, parent, false)
                ExpenseViewHolder(expenseView)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is TransactionItem.TransactionHeader -> HEADER_VIEW_TYPE
            is TransactionItem.TransactionDetail -> {
                val detail = items[position] as TransactionItem.TransactionDetail
                if (detail.transactionType == "Income") {
                    INCOME_VIEW_TYPE
                } else if (detail.transactionType == "Expense") {
                    EXPENSE_VIEW_TYPE
                } else {
                    throw IllegalArgumentException("Invalid transaction type")
                }
            }
            else -> -1
        }
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is HeaderViewHolder -> {
                val headerItem = items[position] as TransactionItem.TransactionHeader
                holder.bind(headerItem)
            }
            is IncomeViewHolder -> {
                val incomeItem = items[position] as TransactionItem.TransactionDetail
                holder.bind(incomeItem)
            }
            is ExpenseViewHolder -> {
                val expenseItem = items[position] as TransactionItem.TransactionDetail
                holder.bind(expenseItem)
            }
        }

        if (position > 0 && items[position] is TransactionItem.TransactionHeader) {
            val layoutParams = holder.itemView.layoutParams as RecyclerView.LayoutParams
            layoutParams.topMargin = 50 // Set the desired margin size
            holder.itemView.layoutParams = layoutParams
        }
    }

    inner class HeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(headerItem: TransactionItem.TransactionHeader) {
            // Bind the header data to the views in the header layout
            val headerTextView = itemView.findViewById<TextView>(R.id.transactionHeader)
            headerTextView.text = SimpleDateFormat("dd MMMM yyyy", Locale.US).format(headerItem.date.toDate())
        }
    }

    inner class IncomeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(incomeItem: TransactionItem.TransactionDetail) {
            // Bind the data to the views in the income layout
            val transactionNameTextView = itemView.findViewById<TextView>(R.id.transactionName)
            val transactionCategoryTextView = itemView.findViewById<TextView>(R.id.transactionCategory)
            val transactionAmountTextView = itemView.findViewById<TextView>(R.id.transactionAmount)

            transactionNameTextView.text = incomeItem.transactionTitle
            transactionCategoryTextView.text = incomeItem.transactionCategories
            transactionAmountTextView.text = String.format("+Rp %,d", incomeItem.transactionAmount)
        }
    }

    inner class ExpenseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(expenseItem: TransactionItem.TransactionDetail) {
            // Bind the data to the views in the expense layout
            val transactionNameTextView = itemView.findViewById<TextView>(R.id.transactionName)
            val transactionCategoryTextView = itemView.findViewById<TextView>(R.id.transactionCategory)
            val transactionAmountTextView = itemView.findViewById<TextView>(R.id.transactionAmount)

            transactionNameTextView.text = expenseItem.transactionTitle
            transactionCategoryTextView.text = expenseItem.transactionCategories
            transactionAmountTextView.text = String.format("-Rp %,d", expenseItem.transactionAmount)
        }
    }

    inner class DataViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(dataItem: TransactionItem.TransactionDetail) {
            // Bind the data to the views in the item layout
            val transactionNameTextView = itemView.findViewById<TextView>(R.id.transactionName)
            val transactionCategoryTextView = itemView.findViewById<TextView>(R.id.transactionCategory)
            val transactionAmountTextView = itemView.findViewById<TextView>(R.id.transactionAmount)

            transactionNameTextView.text = dataItem.transactionTitle
            transactionCategoryTextView.text = dataItem.transactionCategories
            transactionAmountTextView.text = String.format("+Rp %,d", dataItem.transactionAmount)
        }
    }
}
