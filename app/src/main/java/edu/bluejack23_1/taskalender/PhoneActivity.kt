package edu.bluejack23_1.taskalender

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.navigation.ui.AppBarConfiguration
import com.google.android.material.textfield.TextInputEditText
import edu.bluejack23_1.taskalender.databinding.ActivityPhoneBinding

class PhoneActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityPhoneBinding
    private lateinit var phoneInput: TextInputEditText
    private lateinit var sendOtpBtn: Button
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPhoneBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val window = window
        window.statusBarColor = ContextCompat.getColor(this, R.color.background_color)
        window.navigationBarColor = ContextCompat.getColor(this, R.color.background_color)

        phoneInput = findViewById(R.id.phone)
        sendOtpBtn = findViewById(R.id.sendOtpButton)
        progressBar = findViewById(R.id.progress_bar)

        progressBar.visibility = View.GONE

        sendOtpBtn.setOnClickListener { v ->
            val phoneNumber = "+62" + phoneInput.text.toString()

            if (isValidPhoneNumber(phoneNumber)) {
                val phoneFormContainer: CardView = findViewById(R.id.phoneForm)
                phoneFormContainer.alpha = 0f
                val intent = Intent(this, OtpActivity::class.java)
                intent.putExtra("phone", phoneNumber)
                startActivity(intent)
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                finish()
            }else{
                Toast.makeText(this, "Phone invalid", Toast.LENGTH_SHORT).show()
            }
        }

        binding.backLink.setOnClickListener(View.OnClickListener {

            val phoneFormContainer: CardView = findViewById(R.id.phoneForm)
            phoneFormContainer.alpha = 0f
            val i = Intent(this, LoginActivity::class.java)
            startActivity(i)
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            finish()
        })
    }

    fun isValidPhoneNumber(phoneNumber: String): Boolean {
        Log.e("Error: ", phoneNumber)
        return phoneNumber.isNotBlank() && phoneNumber.length >= 4
    }
}