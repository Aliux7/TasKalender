package com.twothreeone.taskalender

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.twothreeone.taskalender.databinding.ActivityLoginBinding
import java.util.regex.Pattern

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityLoginBinding

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
            if(checkAllFields()){
                auth.signInWithEmailAndPassword(email,password).addOnCompleteListener(){
                    if(it.isSuccessful){
                        Toast.makeText(this, "Successfully Sign In", Toast.LENGTH_SHORT).show()
                        val i = Intent(this, MainActivity::class.java)
                        startActivity(i)
                        finish()
                    }else{
                        Log.e("Error: ", it.exception.toString())
                    }
                }
            }
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