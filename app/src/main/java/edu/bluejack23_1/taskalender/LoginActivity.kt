package edu.bluejack23_1.taskalender

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.android.material.textfield.TextInputLayout
import edu.bluejack23_1.taskalender.databinding.ActivityLoginBinding


class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityLoginBinding
    companion object {
        const val PREFS_NAME = "MyPrefsFile"
        const val UID = "UID"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val window = window
        window.statusBarColor = ContextCompat.getColor(this, R.color.background_color)
        window.navigationBarColor = ContextCompat.getColor(this, R.color.background_color)

        supportActionBar?.hide()

        auth = Firebase.auth


        binding.loginButton.setOnClickListener(View.OnClickListener {

            val email = binding.email.text.toString()
            val password = binding.password.text.toString()
            val sharedPreferences = getSharedPreferences(LoginActivity.PREFS_NAME, MODE_PRIVATE)
            if(checkAllFields()){
                auth.signInWithEmailAndPassword(email,password).addOnCompleteListener(){ task ->
                    if(task.isSuccessful){
                        val editor = sharedPreferences.edit()


                        editor.putString("UID", auth.currentUser?.uid.toString())
                        editor.apply()

                        Toast.makeText(this, "Successfully Sign In", Toast.LENGTH_SHORT).show()
                        val i = Intent(this, MainActivity::class.java)
                        startActivity(i)
                        finish()
                    }else{
                        Log.e("Error: ", task.exception.toString())
                        Toast.makeText(this, "Account not found", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })

        binding.registerLink.setOnClickListener(View.OnClickListener {

            val loginFormContainer: CardView = findViewById(R.id.loginForm)
            loginFormContainer.alpha = 0f
            val i = Intent(this, RegisterActivity::class.java)
            startActivity(i)
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            finish()
        })

        binding.btnSignInWithPhone.setOnClickListener(View.OnClickListener {

            val loginFormContainer: CardView = findViewById(R.id.loginForm)
            loginFormContainer.alpha = 0f
            val i = Intent(this, PhoneActivity::class.java)
            startActivity(i)
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            finish()
        })

    }

    private fun checkAllFields(): Boolean {
        val emailContainer = findViewById<TextInputLayout>(R.id.emailContainer)
        val passwordContainer = findViewById<TextInputLayout>(R.id.passwordContainer)

        val emailText = binding.email.text.toString()
        val passwordText = binding.password.text.toString()

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
            passwordContainer.isCounterEnabled = false // Disable the counter
        }

        return true
    }


}