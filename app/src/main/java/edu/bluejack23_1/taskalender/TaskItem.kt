package edu.bluejack23_1.taskalender


class TaskItem(private var name: String, private var deadline: String) {

    // Getter and Setter for name
    fun getName(): String {
        return name
    }

    fun setName(newName: String) {
        name = newName
    }

    //Getter and Setter for deadline
    fun getDeadline(): String{
        return deadline
    }

    fun setDeadline(newDeadline:String){
        deadline = newDeadline
    }


}