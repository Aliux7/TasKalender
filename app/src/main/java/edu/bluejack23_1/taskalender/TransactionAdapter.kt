import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import edu.bluejack23_1.taskalender.TransactionItem
import com.twothreeone.taskalender.R


class TransactionAdapter(private val items: List<TransactionItem>) :
    RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.transaction_item_view, parent, false)
        return TransactionViewHolder(view)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val item = items[position]

        // Bind data to the views in your item layout
        holder.transactionNameTextView.text = item.getName()
        holder.transactionCategoryTextView.text = item.getCategory()
        holder.transactionAmountTextView.text = String.format("+Rp %,d", item.getAmount())
    }

    inner class TransactionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val transactionNameTextView: TextView = itemView.findViewById(R.id.transactionName)
        val transactionCategoryTextView: TextView = itemView.findViewById(R.id.transactionCategory)
        val transactionAmountTextView: TextView = itemView.findViewById(R.id.transactionAmount)
    }
}
