package edu.bluejack23_1.taskalender

class TransactionItem(private var name: String, private var category: String, private var amount: Int, private var flow: String) {

    // Getter and Setter for name
    fun getName(): String {
        return name
    }

    fun setName(newName: String) {
        name = newName
    }

    // Getter and Setter for category
    fun getCategory(): String {
        return category
    }

    fun setCategory(newCategory: String) {
        category = newCategory
    }

    // Getter and Setter for amount
    fun getAmount(): Int {
        return amount
    }

    fun setAmount(newAmount: Int) {
        amount = newAmount
    }

    // Getter and Setter for flow
    fun getFlow(): String {
        return flow
    }

    fun setFlow(newFlow: String) {
        flow = newFlow
    }
}
