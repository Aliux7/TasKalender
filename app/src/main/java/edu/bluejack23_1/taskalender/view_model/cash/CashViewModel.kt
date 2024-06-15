package edu.bluejack23_1.taskalender.view_model.cash

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.mikephil.charting.data.PieEntry
import com.google.firebase.firestore.FirebaseFirestore
import edu.bluejack23_1.taskalender.model.TransactionItem
import kotlin.collections.ArrayList

class CashViewModel : ViewModel() {

    private val transactionListLiveData: LiveData<List<Any>> = MutableLiveData()
    private val pieChartData: MutableLiveData<List<PieEntry>> = MutableLiveData()
    private val categoriesListLiveData: MutableLiveData<List<String>> = MutableLiveData()
    private val db = FirebaseFirestore.getInstance()

    fun loadAndSortTransaction(uid: String){
        val collectionRef = FirebaseFirestore.getInstance().collection("transactions")
        val query = collectionRef.whereEqualTo("userID", uid)
        query.get()
            .addOnSuccessListener { documents ->
                val sortedDocuments = documents.sortedByDescending { it.getTimestamp("transactionDate") }
                val transactionList = mutableListOf<Any>()

                for (document in sortedDocuments) {
                    val transactionDetail = document.toObject(TransactionItem.TransactionDetail::class.java)
                    if (transactionDetail != null) {
                        val transactionDate = transactionDetail.transactionDate
                        if (!transactionList.any { it is TransactionItem.TransactionHeader && it.date.toDate() == transactionDate.toDate() }) {
                            transactionList.add(
                                TransactionItem.TransactionHeader(
                                    transactionDate
                                )
                            )
                        }

                        // Add the transaction data
                        transactionList.add(
                            TransactionItem.TransactionDetail(
                                document.id,
                                transactionDetail.transactionTitle,
                                transactionDetail.transactionCategories,
                                transactionDetail.transactionAmount.toLong(),
                                transactionDetail.transactionType,
                                transactionDetail.transactionDate
                            )
                        )
                    }
                }

                transactionList.sortWith(compareByDescending<Any> {
                    when (it) {
                        is TransactionItem.TransactionDetail -> it.transactionDate.toDate()
                        is TransactionItem.TransactionHeader -> it.date.toDate()
                        else -> TODO()
                    }
                })

                (transactionListLiveData as MutableLiveData).value = transactionList
            }
            .addOnFailureListener { exception ->
                println("Error: $exception")
            }

    }

    fun insertTransaction(newTransaction: TransactionItem.TransactionDetail) {
        val updatedList = (transactionListLiveData.value ?: emptyList()).toMutableList()
        val headerDate = newTransaction.transactionDate.toDate()

        if (!updatedList.any { it is TransactionItem.TransactionHeader && it.date.toDate() == headerDate }) {
            updatedList.add(
                TransactionItem.TransactionHeader(
                    newTransaction.transactionDate
                )
            )
        }

        updatedList.add(newTransaction)
        updatedList.sortWith(compareByDescending<Any> {
            when (it) {
                is TransactionItem.TransactionDetail -> it.transactionDate.toDate()
                is TransactionItem.TransactionHeader -> it.date.toDate()
                else -> TODO()
            }
        })

        (transactionListLiveData as MutableLiveData).value = updatedList
    }


    fun getTransactionListLiveData(): LiveData<List<Any>> {
        return transactionListLiveData
    }

    fun deleteTransaction(transaction: TransactionItem.TransactionDetail) {
        val transactionID = transaction.transactionID
        db.collection("transactions").document(transactionID)
            .delete()

        val currentList = (transactionListLiveData as MutableLiveData).value ?: emptyList()
        val updatedList = currentList.toMutableList()

        val index = updatedList.indexOfFirst { it is TransactionItem.TransactionDetail && it.transactionID == transactionID }

        if (index != -1) {
            updatedList.removeAt(index)
            (transactionListLiveData as MutableLiveData).value = updatedList
        }
    }

//    fun deleteTransactionAndHeader(transactionDetail: TransactionItem.TransactionDetail, headerPosition: Int) {
//        val transactionID = transactionDetail.transactionID
//        db.collection("transactions").document(transactionID).delete()
//
//        val currentList = transactionListLiveData.value ?: emptyList()
//        val updatedList = currentList.toMutableList()
//
//        // Remove the detail
//        val detailIndex = updatedList.indexOfFirst { it is TransactionItem.TransactionDetail && it.transactionID == transactionID }
//        if (detailIndex != -1) {
//            updatedList.removeAt(detailIndex)
//
//            // Check if the header should be removed
//            if (headerPosition != -1) {
//                updatedList.removeAt(headerPosition)
//            }
//
//            (transactionListLiveData as MutableLiveData).value = updatedList
//        }
//    }
    fun deleteTransactionAndHeader(transactionDetail: TransactionItem.TransactionDetail, headerPosition: Int) {
        val transactionID = transactionDetail.transactionID
        db.collection("transactions").document(transactionID).delete()

        val currentList = transactionListLiveData.value ?: emptyList()
        val updatedList = currentList.toMutableList()

        // Remove the detail
        val detailIndex = updatedList.indexOfFirst { it is TransactionItem.TransactionDetail && it.transactionID == transactionID }
        if (detailIndex != -1) {
            updatedList.removeAt(detailIndex)

            // Check if the header should be removed
            if (headerPosition != -1) {
                val header = updatedList[headerPosition] as? TransactionItem.TransactionHeader
                val remainingDetailsCount = getRemainingDetailsCount(updatedList, headerPosition)

                // Check if there is only one detail under the header
                if (remainingDetailsCount == 0) {
                    updatedList.removeAt(headerPosition)
                }

                (transactionListLiveData as MutableLiveData).value = updatedList
            }
        }
    }

    private fun getRemainingDetailsCount(list: List<Any>, headerPosition: Int): Int {
        var count = 0
        for (i in headerPosition + 1 until list.size) {
            if (list[i] is TransactionItem.TransactionDetail) {
                count++
            } else {
                // Break if a header is encountered
                break
            }
        }
        return count
    }


    fun getTransactionAtPosition(position: Int): Any? {
        val currentList = transactionListLiveData.value

        if (currentList != null && position in currentList.indices) {
            return currentList[position]
        }

        return null
    }

    fun updatePieChartData(outcome: Float, income: Float) {
        val entries = ArrayList<PieEntry>()
        entries.add(PieEntry(outcome, ""))
        entries.add(PieEntry(income, ""))

        pieChartData.value = entries
    }

    fun getPieChartData(): LiveData<List<PieEntry>> {
        return pieChartData
    }

    fun fetchCategories(uid: String) {
        val categories = ArrayList<String>()

        db.collection("categories")
            .whereIn("access", listOf("Public", uid))
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val category = document.getString("name")
                    category?.let { categories.add(it) }
                }
                categories.add("New Category")


                categoriesListLiveData.value = categories
            }
    }

    fun getCategoriesListLiveData(): LiveData<List<String>> {
        return categoriesListLiveData
    }
}