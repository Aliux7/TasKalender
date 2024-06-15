package edu.bluejack23_1.taskalender

import edu.bluejack23_1.taskalender.view_model.cash.TransactionAdapter
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import edu.bluejack23_1.taskalender.model.TransactionItem
import edu.bluejack23_1.taskalender.view_model.cash.CashViewModel
import edu.bluejack23_1.taskalender.view_model.cash.SwipeToDeleteCallback
import java.text.SimpleDateFormat
import java.util.*


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class CashFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var viewModel: CashViewModel

    private lateinit var recyclerView: RecyclerView
    private lateinit var addBtn: FloatingActionButton
    private var formatDate = SimpleDateFormat("dd MMMM YYYY", Locale.US)
    private var dateTextInput: TextView? = null
    private var dateTextInputLayout: TextInputLayout? = null
    private val db = FirebaseFirestore.getInstance()
    private lateinit var adapter: TransactionAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.fragment_cash, container, false)
        recyclerView = root.findViewById(R.id.transactionRecyclerView)

        val sharedPreferences = requireContext().getSharedPreferences(
            LoginActivity.PREFS_NAME,
            Context.MODE_PRIVATE
        )
        val uid = sharedPreferences.getString(LoginActivity.UID, null)

        val pieChart = root.findViewById<PieChart>(R.id.pieChart_view)

        val incomeTxt = root.findViewById<TextView>(R.id.totalIncomeTxt)
        val outcomeTxt = root.findViewById<TextView>(R.id.totalOutcomeTxt)
        val amountTxt = root.findViewById<TextView>(R.id.totalAmountTxt)
        var totalIncome = 0.0
        var totalOutcome = 0.0

        viewModel = ViewModelProvider(this)[CashViewModel::class.java]
        viewModel.loadAndSortTransaction(uid.toString())
        viewModel.getTransactionListLiveData().observe(viewLifecycleOwner) { transactionList ->
            if (transactionList.isNotEmpty()) {
                totalIncome = 0.0
                totalOutcome = 0.0
                for (transaction in transactionList) {
                    if (transaction is TransactionItem.TransactionDetail) {
                        if(transaction.transactionType == "Income"){
                            totalIncome += transaction.transactionAmount
                        }else if(transaction.transactionType == "Expense"){
                            totalOutcome += transaction.transactionAmount
                        }
                    }
                }

                if(totalIncome >= totalOutcome){
                    amountTxt.text = "+Rp.${String.format("%,.0f", totalIncome - totalOutcome)}"
                }else{
                    amountTxt.text = "-Rp.${String.format("%,.0f", totalOutcome - totalIncome)}"
                }
                incomeTxt.text = "+Rp.${String.format("%,.0f", totalIncome)}"
                outcomeTxt.text = "-Rp.${String.format("%,.0f", totalOutcome)}"

                adapter = TransactionAdapter(transactionList as ArrayList<Any>)
                recyclerView.adapter = adapter
                recyclerView.layoutManager = LinearLayoutManager(requireContext())

                val swipeToDeleteCallback = SwipeToDeleteCallback(viewModel, recyclerView)
                val itemTouchHelper = ItemTouchHelper(swipeToDeleteCallback)
                itemTouchHelper.attachToRecyclerView(recyclerView)

                viewModel.updatePieChartData(totalOutcome.toFloat(), totalIncome.toFloat())
                viewModel.getPieChartData().observe(viewLifecycleOwner) { pieEntries ->
                    val customColors = ArrayList<Int>()
                    customColors.add(Color.parseColor("#eb5757"))
                    customColors.add(Color.parseColor("#6fcf97"))

                    val pieDataSet = PieDataSet(pieEntries, "")
                    pieDataSet.colors = customColors

                    val pieData = PieData(pieDataSet)
                    pieChart.legend.isEnabled = false
                    pieChart.data = pieData
                    pieChart.holeRadius = 0.7f
                    pieChart.description = null
                    pieChart.isDrawHoleEnabled = false
                    pieDataSet.valueTextSize = 0f
                    pieDataSet.valueTextColor = Color.TRANSPARENT

                    pieChart.animateY(1000)
                    pieChart.invalidate()
                }
            }
        }

        addBtn = root.findViewById(R.id.addTransactionBtn)
        addBtn.setOnClickListener{
            showDialog()
        }

        return root;
    }

    private fun showDialog() {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.add_transaction_view)

        val sharedPreferences = requireContext().getSharedPreferences(
            LoginActivity.PREFS_NAME,
            Context.MODE_PRIVATE
        )
        val uid = sharedPreferences.getString(LoginActivity.UID, null)

        val btnIncome = dialog.findViewById<Button>(R.id.btnIncome)
        val btnExpense = dialog.findViewById<Button>(R.id.btnExpense)

        val rupiahTextInputLayout = dialog.findViewById<TextInputLayout>(R.id.rupiahContainer)
        val rupiahTextInput = dialog.findViewById<TextInputEditText>(R.id.rupiah)
        rupiahTextInput.setText("0")

        val titleTextInputLayout = dialog.findViewById<TextInputLayout>(R.id.titleContainer)
        val titleTextInput = dialog.findViewById<TextInputEditText>(R.id.title)


        val categoriesAutoCompleteTextView = dialog.findViewById<AutoCompleteTextView>(R.id.categories)
        viewModel.fetchCategories(uid.toString());
        viewModel.getCategoriesListLiveData().observe(viewLifecycleOwner) { categories ->
            if (categories.isNotEmpty()) {
                val arrayAdapter = ArrayAdapter(
                    requireContext(),
                    R.layout.transaction_list_categories_view,
                    categories
                )
                categoriesAutoCompleteTextView.setAdapter(arrayAdapter)
                categoriesAutoCompleteTextView.setDropDownBackgroundDrawable(context?.getResources()?.getDrawable(
                    R.color.background_color
                ))
                categoriesAutoCompleteTextView.setText("Others", false)

                categoriesAutoCompleteTextView.setOnItemClickListener { _, _, position, _ ->
                    val selectedCategory = categories[position]

                    if (selectedCategory == "New Category") {
                        newCategoryDialog()
                        dialog.dismiss()
                    }
                }
            }
        }

        val addButton = dialog.findViewById<Button>(R.id.addButton)

        btnIncome.isSelected = true
        btnIncome.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.accent_color))
        btnExpense.setBackgroundColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.transparent_color
            )
        )

        btnIncome.setOnClickListener {
            if (!btnIncome.isSelected) {
                btnIncome.isSelected = true
                btnIncome.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.accent_color
                    )
                )
                btnExpense.isSelected = false
                btnExpense.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.transparent_color
                    )
                )
            }
        }

        btnExpense.setOnClickListener {
            if (!btnExpense.isSelected) {
                btnExpense.isSelected = true
                btnExpense.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.accent_color
                    )
                )
                btnIncome.isSelected = false
                btnIncome.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.transparent_color
                    )
                )
            }
        }

        dateTextInputLayout = dialog.findViewById(R.id.dateContainer)
        dateTextInput = dialog.findViewById(R.id.date)
        val today = Calendar.getInstance()
        dateTextInput?.text = formatDate.format(today.time)

        dateTextInputLayout?.setOnClickListener {
            val getDate = Calendar.getInstance()
            val datePicker = DatePickerDialog(
                requireContext(),
                android.R.style.Theme_Holo_Dialog,
                DatePickerDialog.OnDateSetListener { datePicker, i, i2, i3 ->
                    val selectDate = Calendar.getInstance()
                    selectDate.set(Calendar.YEAR, i)
                    selectDate.set(Calendar.MONTH, i2)
                    selectDate.set(Calendar.DAY_OF_MONTH, i3)
                    val date = formatDate.format(selectDate.time)
                    dateTextInput?.text = date
                },
                getDate.get(Calendar.YEAR),
                getDate.get(Calendar.MONTH),
                getDate.get(Calendar.DAY_OF_MONTH)
            )
            datePicker.show();
        }

        addButton.setOnClickListener {
            // Validate and get input data
            val selectedType = if (btnIncome.isSelected) "Income" else "Expense"
            var rupiah = rupiahTextInput.text.toString()
            val title = titleTextInput.text.toString()
            val category = categoriesAutoCompleteTextView.text.toString()

            val sharedPreferences = requireContext().getSharedPreferences(
                LoginActivity.PREFS_NAME,
                Context.MODE_PRIVATE
            )
            val uid = sharedPreferences.getString(LoginActivity.UID, null)

            val date = dateTextInput?.text.toString()
            val inputFormat = SimpleDateFormat("dd MMMM yyyy", Locale.US)
            val dateFormat = inputFormat.parse(date)
            val timestamp = dateFormat?.time

            if (!date.equals("Date Transaction") && title.isNotEmpty() && rupiah.isNotEmpty() && category.isNotEmpty() && rupiah.toDoubleOrNull()?.let { it > 0 } == true) {
                val transactionData: Map<String, Any> = hashMapOf(
                    "userID" to uid.toString(),
                    "transactionType" to selectedType,
                    "transactionDate" to Timestamp(Date(timestamp ?: 0)),
                    "transactionTitle" to title,
                    "transactionAmount" to rupiah.toDouble(),
                    "transactionCategories" to category
                )

                db.collection("transactions").add(transactionData)
                    .addOnSuccessListener { documentReference ->
                        val newTransactionID = documentReference.id
                        val newTransaction = TransactionItem.TransactionDetail(
                            newTransactionID,
                            title,
                            category,
                            rupiah.toLong(),
                            selectedType,
                            Timestamp(Date(timestamp ?: 0))
                        )

                        viewModel.insertTransaction(newTransaction)
                        adapter.notifyDataSetChanged()
                    }
                Toast.makeText(requireContext(), "Add Transaction Successful", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            } else {
                if (date.equals("Date Transaction")) {
                    dateTextInputLayout?.error = "Date is required"
                }
                if (rupiah.isEmpty()) {
                    rupiahTextInputLayout?.error = "Amount is required"
                }
                if(rupiah.toDoubleOrNull()?.let { it < 0 } == true){
                    rupiahTextInputLayout?.error = "Amount cannot be negative"
                }
                if(title.isEmpty()){
                    titleTextInputLayout?.error = "Title is required"
                }
            }
        }

        dialog.show()
        val window = dialog.window
        window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        window?.attributes?.windowAnimations = R.style.DialogAnimation
        window?.setGravity(Gravity.BOTTOM)
    }

    private fun newCategoryDialog() {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.add_category_view)

        val sharedPreferences = requireContext().getSharedPreferences(
            LoginActivity.PREFS_NAME,
            Context.MODE_PRIVATE
        )
        val uid = sharedPreferences.getString(LoginActivity.UID, null)

        val categoryTextInputLayout = dialog.findViewById<TextInputLayout>(R.id.categoryContainer)
        val categoryTextInput = dialog.findViewById<TextInputEditText>(R.id.category)
        val addButton = dialog.findViewById<Button>(R.id.addButton)


        addButton.setOnClickListener {
            val category = categoryTextInput.text.toString()


            val categoryData: Map<String, Any> = hashMapOf(
                "access" to uid.toString(),
                "name" to category
            )

            if (category.isNotEmpty()) {
                viewModel.getCategoriesListLiveData().observe(viewLifecycleOwner) { categories ->
                    if (categories.isNotEmpty()) {
                        val categoryName = category.toLowerCase(Locale.getDefault())

                        if (categories.any { it.toLowerCase(Locale.getDefault()) == categoryName }) {
                            categoryTextInputLayout?.error = "Category already exists"
                        } else {
                            db.collection("categories").add(categoryData)
                                .addOnSuccessListener { documentReference ->
                                    Toast.makeText(requireContext(), "Add Category Successful", Toast.LENGTH_SHORT).show()
                                    dialog.dismiss()
                                }
                        }
                    }
                }

            } else {
                if(category.isEmpty()){
                    categoryTextInputLayout?.error = "Category is required"
                }
            }
        }

        dialog.show()
        val window = dialog.window
        window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        window?.attributes?.windowAnimations = R.style.DialogAnimation
        window?.setGravity(Gravity.BOTTOM)
    }
}