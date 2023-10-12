package edu.bluejack23_1.taskalender

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.ui.AppBarConfiguration
import com.twothreeone.taskalender.databinding.ActivitySplashBinding
import android.widget.ImageView
import androidx.core.content.ContextCompat
import com.twothreeone.taskalender.R

class SplashActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val window = window
        window.statusBarColor = ContextCompat.getColor(this, R.color.background_color)
        window.navigationBarColor = ContextCompat.getColor(this, R.color.background_color)

        val tkLogoImageView: ImageView = findViewById(R.id.tk_logo)
        tkLogoImageView.alpha = 0f
        tkLogoImageView.animate().setDuration(1500).alpha(1f).withEndAction(){
            val i = Intent(this, LoginActivity::class.java)
            startActivity(i)
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            finish()
        }
    }
}