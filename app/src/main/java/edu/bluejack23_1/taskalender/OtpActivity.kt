package edu.bluejack23_1.taskalender

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*

import com.google.firebase.auth.ktx.auth
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.ui.AppBarConfiguration
import com.google.firebase.FirebaseException
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import edu.bluejack23_1.taskalender.databinding.ActivityOtpBinding
import java.util.*
import java.util.concurrent.TimeUnit

class OtpActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityOtpBinding
    private var phoneNumber: String? = null
    private var timeoutSeconds = 60L
    private var verificationCode: String? = null
    private lateinit var resendingToken: PhoneAuthProvider.ForceResendingToken

    private lateinit var otpInput: EditText
    private lateinit var loginBtn: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var resendOtpTextView: TextView
    private val db = FirebaseFirestore.getInstance()
    private val mAuth: FirebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityOtpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val window = window
        window.statusBarColor = ContextCompat.getColor(this, R.color.background_color)
        window.navigationBarColor = ContextCompat.getColor(this, R.color.background_color)

        otpInput = findViewById(R.id.otp)
        loginBtn = findViewById(R.id.loginButton)
        progressBar = findViewById(R.id.login_progress_bar)
        resendOtpTextView = findViewById(R.id.resendOtp)
        progressBar.visibility = View.GONE
        phoneNumber = intent.extras?.getString("phone")

        sendOtp(phoneNumber, false)

        loginBtn.setOnClickListener(View.OnClickListener { v: View? ->
            val enteredOtp = otpInput.text.toString()
            val credential =
                PhoneAuthProvider.getCredential(verificationCode!!, enteredOtp)
            signIn(credential)
        })
        Toast.makeText(applicationContext, phoneNumber, Toast.LENGTH_LONG).show()
    }

    private fun sendOtp(phoneNumber: String?, isResend: Boolean) {
        startResendTimer()
        setInProgress(true)
        val builder = PhoneAuthOptions.newBuilder(mAuth)
            .setPhoneNumber(phoneNumber!!)
            .setTimeout(timeoutSeconds, TimeUnit.SECONDS)
            .setActivity(this)
            .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {
                    signIn(phoneAuthCredential)
                    setInProgress(false)
                }

                override fun onVerificationFailed(e: FirebaseException) {
                    val errorMessage = e.message
                    Log.e("Error: Babi ", errorMessage.toString())
                    Toast.makeText(applicationContext, "OTP verification failed: $errorMessage", Toast.LENGTH_SHORT).show()
                    setInProgress(false)
                }


                override fun onCodeSent(s: String, forceResendingToken: PhoneAuthProvider.ForceResendingToken) {
                    super.onCodeSent(s, forceResendingToken)
                    verificationCode = s
                    resendingToken = forceResendingToken
                    Toast.makeText(applicationContext, "OTP sent successfully", Toast.LENGTH_SHORT).show()
                    setInProgress(false)
                }
            })
        if (isResend) {
            PhoneAuthProvider.verifyPhoneNumber(builder.setForceResendingToken(resendingToken).build())
        } else {
            PhoneAuthProvider.verifyPhoneNumber(builder.build())
        }
    }

    private fun signIn(phoneAuthCredential: PhoneAuthCredential) {
        setInProgress(true)
        val userData: Map<String, Any> = hashMapOf(
            "email" to "",
            "username" to "",
            "phone" to phoneNumber.toString(),
            "dob" to Timestamp.now()
        )

        val sharedPreferences = getSharedPreferences(LoginActivity.PREFS_NAME, MODE_PRIVATE)
        mAuth.signInWithCredential(phoneAuthCredential).addOnCompleteListener { task ->
            setInProgress(false)
            if (task.isSuccessful) {
                val editor = sharedPreferences.edit()

                val user = mAuth.currentUser
                val userId = user?.uid
                if(userId != null){
                    val userDocRef = db.collection("users").document(userId)
                    userDocRef.get().addOnCompleteListener { documentSnapshotTask ->
                        if (documentSnapshotTask.isSuccessful) {
                            val document = documentSnapshotTask.result
                            if (document != null && document.exists()) {
                                // Document with the user ID already exists, do nothing.
                            } else {
                                db.collection("users").document(userId).set(userData)
                            }
                        } else {

                        }
                    }
                }

                editor.putString("UID", mAuth.currentUser?.uid.toString())
                editor.apply()
                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra("phone", phoneNumber)
                startActivity(intent)
            } else {
                Toast.makeText(this, "OTP verification failed2", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setInProgress(inProgress: Boolean) {
        if (inProgress) {
            progressBar.visibility = View.VISIBLE
            loginBtn.visibility = View.GONE
        } else {
            progressBar.visibility = View.GONE
            loginBtn.visibility = View.VISIBLE
        }
    }

    private fun startResendTimer() {
        resendOtpTextView.isEnabled = false
        val timer = Timer()
        timer.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                timeoutSeconds--
                runOnUiThread {
                    resendOtpTextView.text = "Resend OTP in $timeoutSeconds seconds"
                }
                if (timeoutSeconds <= 0) {
                    timeoutSeconds = 60L
                    timer.cancel()
                    runOnUiThread {
                        resendOtpTextView.isEnabled = true
                    }
                }
            }
        }, 0, 1000)
    }

}