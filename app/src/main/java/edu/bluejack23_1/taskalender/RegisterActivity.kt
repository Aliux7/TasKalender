package edu.bluejack23_1.taskalender

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import edu.bluejack23_1.taskalender.databinding.ActivityRegisterBinding
import java.text.SimpleDateFormat
import java.util.*

class RegisterActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityRegisterBinding
    private var datePickerDialog: DatePickerDialog? = null
    private var dobTextInputLayout: TextInputLayout? = null
    private var dobTextInput: TextView? = null
    private var formatDate = SimpleDateFormat("dd MMMM YYYY", Locale.US)
    private val db = FirebaseFirestore.getInstance()
    private val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth
        val window = window
        window.statusBarColor = ContextCompat.getColor(this, R.color.background_color)
        window.navigationBarColor = ContextCompat.getColor(this, R.color.background_color)
        supportActionBar?.hide()

        dobTextInputLayout = findViewById(R.id.dobContainer)
        dobTextInput = findViewById(R.id.dob)
        dobTextInputLayout?.setOnClickListener {
            val getDate = Calendar.getInstance()
            val datePicker = DatePickerDialog(this, android.R.style.Theme_Holo_Dialog, DatePickerDialog.OnDateSetListener { datePicker, i, i2, i3 ->
                val selectDate = Calendar.getInstance()
                selectDate.set(Calendar.YEAR, i)
                selectDate.set(Calendar.MONTH, i2)
                selectDate.set(Calendar.DAY_OF_MONTH, i3)
                val date = formatDate.format(selectDate.time)
                dobTextInput?.text = date
            }, getDate.get(Calendar.YEAR), getDate.get(Calendar.MONTH), getDate.get(Calendar.DAY_OF_MONTH))
            datePicker.show();
        }

        val today = Calendar.getInstance()
        dobTextInput?.text = formatDate.format(today.time)

        binding.registerButton.setOnClickListener(View.OnClickListener {
            val emailText = binding.email.text.toString()
            val usernameText = binding.username.text.toString()
            val phoneText = "+62" + binding.phone.text.toString()
            val passwordText = binding.password.text.toString()
            val dobText = binding.dob.text.toString()
            val inputFormat = SimpleDateFormat("dd MMMM yyyy", Locale.US)
            val dateFormat = inputFormat.parse(dobText)
            val timestamp = dateFormat?.time


            val userData: Map<String, Any> = hashMapOf(
                "email" to emailText,
                "username" to usernameText,
                "phone" to phoneText,
                "dob" to Timestamp(Date(timestamp ?: 0))
            )

            if(checkAllFields()){
                auth.createUserWithEmailAndPassword(emailText, passwordText)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            val user = auth.currentUser
                            val userId = user?.uid
                            if (userId != null) {
                                Log.d("User ID", userId)
                                db.collection("users").document(userId).set(userData)
                            }

                            Toast.makeText(this, "Registration Successful", Toast.LENGTH_SHORT).show()
                            val i = Intent(this, LoginActivity::class.java)
                            startActivity(i)
                            finish()
                        } else {
                            Log.e("Error: ", task.exception.toString())
                            Toast.makeText(this, "Registration failed", Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        })

        binding.loginLink.setOnClickListener(View.OnClickListener {
            val registerFormContainer: CardView = findViewById(R.id.registerForm)
            registerFormContainer.alpha = 0f
            val i = Intent(this, LoginActivity::class.java)
            startActivity(i)
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            finish()
        })
    }

    private fun checkAllFields(): Boolean {
        val emailContainer = findViewById<TextInputLayout>(R.id.emailContainer)
        val usernameContainer = findViewById<TextInputLayout>(R.id.usernameContainer)
        val phoneContainer = findViewById<TextInputLayout>(R.id.phoneContainer)
        val passwordContainer = findViewById<TextInputLayout>(R.id.passwordContainer)
        val confirmPasswordContainer = findViewById<TextInputLayout>(R.id.passwordConfirmationContainer)
        val dobContainer = findViewById<TextInputLayout>(R.id.dobContainer)

        val emailText = binding.email.text.toString()
        val usernameText = binding.username.text.toString()
        val phoneText = binding.phone.text.toString()
        val passwordText = binding.password.text.toString()
        val confirmPasswordText = binding.confirmationPassword.text.toString()
        val dobText = binding.dob.text.toString()

        val emailPattern = Patterns.EMAIL_ADDRESS
        if (emailText.isEmpty() || emailText.length > 50) {
            emailContainer.error = if (emailText.isEmpty()) "Required*" else null
            emailContainer.isCounterEnabled = emailText.length > 50
            return false
        } else if (!emailPattern.matcher(emailText).matches()) {
            emailContainer.error = "Invalid email format"
            return false
        } else {
            emailContainer.error = null
            emailContainer.isCounterEnabled = false
        }

        if (usernameText.isEmpty() || usernameText.length > 50) {
            usernameContainer.error = if (usernameText.isEmpty()) "Required*" else null
            usernameContainer.isCounterEnabled = usernameText.length > 50
            return false
        } else {
            usernameContainer.error = null
            usernameContainer.isCounterEnabled = false
        }

        if (phoneText.isEmpty() || phoneText.length > 20) {
            phoneContainer.error = if (phoneText.isEmpty()) "Required*" else null
            phoneContainer.isCounterEnabled = phoneText.length > 20
            return false
        } else {
            phoneContainer.error = null
            phoneContainer.isCounterEnabled = false
        }

        if (passwordText.isEmpty() || passwordText.length > 50) {
            passwordContainer.error = if (passwordText.isEmpty()) "Required*" else null
            passwordContainer.isCounterEnabled = passwordText.length > 50
            return false
        } else {
            passwordContainer.error = null
            passwordContainer.isCounterEnabled = false
        }

        if (confirmPasswordText.isEmpty() || confirmPasswordText != passwordText) {
            confirmPasswordContainer.error = if (confirmPasswordText.isEmpty()) "Required*" else "Passwords do not match"
            return false
        } else {
            confirmPasswordContainer.error = null
        }

        if (dobText.isEmpty()) {
            dobContainer.error = "Required*"
            return false
        } else {
            dobContainer.error = null
        }

        return true
    }

}