package edu.bluejack23_1.taskalender.view_model.profile

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.Timestamp
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import edu.bluejack23_1.taskalender.model.User

class ProfileViewModel : ViewModel() {

    private val userInfoLiveData: MutableLiveData<User?> = MutableLiveData()
    private val db = FirebaseFirestore.getInstance()
    private val firebaseAuth = FirebaseAuth.getInstance()

    fun fetchUserInfo(uid: String) {
        db.collection("users")
            .document(uid)
            .get()
            .addOnSuccessListener { documentSnapshot ->
                val user = documentSnapshot.toObject(User::class.java)

                if (user != null && documentSnapshot.exists()) {
                    userInfoLiveData.value = user
                } else {
                    userInfoLiveData.value = User()
                }
            }
    }

    fun updateUserProfile(uid: String?, newUsername: String, newDob: Timestamp) {
        if (uid != null) {
            val userUpdate = hashMapOf(
                "username" to newUsername,
                "dob" to newDob
            )

            db.collection("users")
                .document(uid)
                .update(userUpdate as Map<String, Any>)
        }
    }

    fun updatePassword(oldPassword: String, newPassword: String, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        val user = firebaseAuth.currentUser
        val authCredential: AuthCredential = EmailAuthProvider.getCredential(user?.email.orEmpty(), oldPassword)
        user?.reauthenticate(authCredential)?.addOnSuccessListener {
            user.updatePassword(newPassword).addOnSuccessListener {
                onSuccess()
            }.addOnFailureListener { e ->
                onFailure(e.message.orEmpty())
            }
        }?.addOnFailureListener { e ->
            onFailure(e.message.orEmpty())
        }
    }


    fun getUserInfoLiveData(): MutableLiveData<User?> {
        return userInfoLiveData
    }

}