package com.twothreeone.taskalender

import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import android.view.Menu
import android.view.MenuItem
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.etebarian.meowbottomnavigation.MeowBottomNavigation
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.twothreeone.taskalender.databinding.ActivityMainBinding

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

        changeFragment(HomeFragment());

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
                    changeFragment(CashFlowFragment());
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

//        binding.bottomNavigation.setOnItemSelectedListener { menuItem ->
//            when (menuItem.itemId) {
//                R.id.bottom_home -> {
////                    val i = Intent(this, MainActivity::class.java)
////                    startActivity(i)
////                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
////                    finish()
//
//                    changeFragment(HomeFragment());
//                    true
//                }
//                R.id.bottom_cash -> {
//                    // Handle the "bottom_cash" item selection here
//                    changeFragment(CashFlowFragment());
//                    true
//                }
//                R.id.bottom_task -> {
//                    // Handle the "bottom_cash" item selection here
//                    changeFragment(TodoListFragment());
//                    true
//                }
//                R.id.bottom_note -> {
//                    // Handle the "bottom_cash" item selection here
//                    changeFragment(NotesFragment());
//                    true
//                }
//                R.id.bottom_profile -> {
////                    val i = Intent(this, ProfileActivity::class.java)
////                    startActivity(i)
////                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
////                    finish()
//                    changeFragment(ProfileFragment());
//                    true
//                }
//                else -> false
//            }
//        }
    }

    private fun changeFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_container, fragment)
        fragmentTransaction.commit()
    }
}