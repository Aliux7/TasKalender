package edu.bluejack23_1.taskalender

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.twothreeone.taskalender.R
import com.twothreeone.taskalender.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityLoginBinding
    private lateinit var googleSignInClient:GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val window = window
        window.statusBarColor = ContextCompat.getColor(this, R.color.background_color)
        window.navigationBarColor = ContextCompat.getColor(this, R.color.background_color)

        supportActionBar?.hide()

        auth = Firebase.auth
//        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN)
//            .requestIdToken(getString(R.string.default_web_client_id))

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