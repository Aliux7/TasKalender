package edu.bluejack23_1.taskalender

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.twothreeone.taskalender.R
import com.twothreeone.taskalender.databinding.ActivityRegisterBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

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

        binding.registerButton.setOnClickListener(View.OnClickListener {
            val emailText = binding.email.text.toString()
            val usernameText = binding.username.text.toString()
            val phoneText = binding.phone.text.toString()
            val passwordText = binding.password.text.toString()
            val confirmPasswordText = binding.confirmationPassword.text.toString()
            val dobText = binding.dob.text.toString()

            val userData: Map<String, Any> = hashMapOf(
                "email" to emailText,
                "username" to usernameText,
                "phone" to phoneText,
                "dob" to dobText
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

        if (emailText.isEmpty() || emailText.length > 50) {
            emailContainer.error = if (emailText.isEmpty()) "Required*" else null
            emailContainer.isCounterEnabled = emailText.length > 50
            return false
        } else {
            emailContainer.error = null
            emailContainer.isCounterEnabled = false // Disable the counter
        }

        if (passwordText.isEmpty() || passwordText.length > 50) {
            passwordContainer.error = if (passwordText.isEmpty()) "Required*" else null
            passwordContainer.isCounterEnabled = passwordText.length > 50
            return false
        } else {
            passwordContainer.error = null
            passwordContainer.isCounterEnabled = false
        }

        return true
    }

}