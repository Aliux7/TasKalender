package edu.bluejack23_1.taskalender.model

import com.google.firebase.Timestamp

sealed class TransactionItem {
    data class TransactionDetail(
        val transactionID: String,
        val transactionTitle: String,
        val transactionCategories: String,
        val transactionAmount: Long,
        val transactionType: String,
        val transactionDate: Timestamp
    ) {
        // Default no-argument constructor
        constructor() : this("", "", "", 0, "", Timestamp.now())
    }
    data class TransactionHeader(
        val date: Timestamp
    )

}

