package edu.bluejack23_1.taskalender

import TransactionAdapter
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.twothreeone.taskalender.R
import com.twothreeone.taskalender.databinding.ActivityRegisterBinding
import com.twothreeone.taskalender.databinding.FragmentCashFlowBinding

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
private var binding: FragmentCashFlowBinding? = null

class CashFlowFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    private var binding: FragmentCashFlowBinding? = null
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

        val transactionItems = listOf(
            TransactionItem("Assistant Lab Salary", "Main Job", 5300000, "Income"),
            TransactionItem("Assistant Lab Salary", "Main Job", 5300000, "Income"),
            TransactionItem("Assistant Lab Salary", "Main Job", 5300000, "Income"),
            TransactionItem("Assistant Lab Salary", "Main Job", 5300000, "Income"),
            TransactionItem("Assistant Lab Salary", "Main Job", 5300000, "Income"),
            TransactionItem("Assistant Lab Salary", "Main Job", 5300000, "Income"),
            TransactionItem("Assistant Lab Salary", "Main Job", 5300000, "Income")
        )


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val transactionItems = listOf(
            TransactionItem("Assistant Lab Salary", "Main Job", 5300000, "Income"),
            TransactionItem("Assistant Lab Salary", "Main Job", 5300000, "Income"),
            TransactionItem("Assistant Lab Salary", "Main Job", 5300000, "Income"),
            TransactionItem("Assistant Lab Salary", "Main Job", 5300000, "Income"),
            TransactionItem("Assistant Lab Salary", "Main Job", 5300000, "Income"),
            TransactionItem("Assistant Lab Salary", "Main Job", 5300000, "Income"),
            TransactionItem("Assistant Lab Salary", "Main Job", 5300000, "Income"),
            TransactionItem("Assistant Lab Salary", "Main Job", 5300000, "Income"),
            TransactionItem("Assistant Lab Salary", "Main Job", 5300000, "Income"),
            TransactionItem("Assistant Lab Salary", "Main Job", 5300000, "Income"),
            TransactionItem("Assistant Lab Salary", "Main Job", 5300000, "Income"),
            TransactionItem("Assistant Lab Salary", "Main Job", 5300000, "Income"),
            TransactionItem("Assistant Lab Salary", "Main Job", 5300000, "Income"),
            TransactionItem("Assistant Lab Salary", "Main Job", 5300000, "Income"),
            TransactionItem("Assistant Lab Salary", "Main Job", 5300000, "Income"),
            TransactionItem("Assistant Lab Salary", "Main Job", 5300000, "Income"),
            TransactionItem("Assistant Lab Salary", "Main Job", 5300000, "Income"),
            TransactionItem("Assistant Lab Salary", "Main Job", 5300000, "Income"),
            TransactionItem("Assistant Lab Salary", "Main Job", 5300000, "Income"),
            TransactionItem("Assistant Lab Salary", "Main Job", 5300000, "Income"),
            TransactionItem("Assistant Lab Salary", "Main Job", 5300000, "Income"),
        )

        val root = inflater.inflate(R.layout.fragment_cash_flow, container, false)
        recyclerView = root.findViewById(R.id.transactionRecyclerView)
        val adapter = TransactionAdapter(transactionItems)

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())


        return root;
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}