package edu.bluejack23_1.taskalender

import android.app.AlertDialog
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.ui.AppBarConfiguration
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.etebarian.meowbottomnavigation.MeowBottomNavigation
import com.google.firebase.firestore.FirebaseFirestore
import edu.bluejack23_1.taskalender.databinding.ActivityMainBinding
import android.provider.Settings

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private val TAG = "MainActivity"
    val db = FirebaseFirestore.getInstance()
    private val ID_HOME = 1
    private val ID_CASH_FLOW = 2
    private val ID_TODO_LIST = 3
    private val ID_NOTES = 4
    private val ID_PROFILE = 5
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val window = window
        window.statusBarColor = ContextCompat.getColor(this, R.color.background_color)
        window.navigationBarColor = ContextCompat.getColor(this, R.color.background_color)
        supportActionBar?.hide()

        if (!isNotificationPermissionEnabled()) {
            showNotificationPermissionDialog()
        }
        
        val targetFragment = intent.getStringExtra("targetFragment")

        if (targetFragment == "NotesFragment") {
            changeFragment(NotesListFragment());
            intent.removeExtra("targetFragment")
        }
        else{
            changeFragment(HomeFragment());
        }
//        changeFragment(AddTransactionFragment());

        binding.bottomNavigation.show(0)
        binding.bottomNavigation.add(MeowBottomNavigation.Model(0, R.drawable.baseline_home_24))
        binding.bottomNavigation.add(MeowBottomNavigation.Model(1, R.drawable.baseline_cash_24))
        binding.bottomNavigation.add(MeowBottomNavigation.Model(2, R.drawable.baseline_task_alt_24))
        binding.bottomNavigation.add(MeowBottomNavigation.Model(3, R.drawable.baseline_note_24))
        binding.bottomNavigation.add(MeowBottomNavigation.Model(4, R.drawable.baseline_profile_24))

        binding.bottomNavigation.setOnClickMenuListener {
            when(it.id){
                0 -> {
                    changeFragment(HomeFragment());
                }
                1 -> {
                    changeFragment(CashFragment());
                }
                2 -> {
                    changeFragment(TodoListFragment());
                }
                3 -> {
                    changeFragment(NotesFragment());
                }
                4 -> {
                    changeFragment(ProfileFragment());
                }
            }
        }
    }


    private fun isNotificationPermissionEnabled(): Boolean {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        return notificationManager.areNotificationsEnabled()
    }


    private fun showNotificationPermissionDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Enable Notifications")
        builder.setMessage("To receive notifications, please enable notifications for this app in your device settings.")
        builder.setPositiveButton("Go to Settings") { _, _ ->
            // Open the app's notification settings in the device settings
            val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
            intent.putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
            startActivity(intent)
        }
        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }
        builder.setCancelable(false)  // Prevent the user from dismissing the dialog without taking action
        builder.show()
    }

    private fun changeFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_container, fragment)
        fragmentTransaction.commit()
    }
}