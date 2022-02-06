package com.example.instagramclone

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.instagramclone.fragments.HomeFragment
import com.example.instagramclone.fragments.NotificationFragment
import com.example.instagramclone.fragments.ProfileFragment
import com.example.instagramclone.fragments.SearchFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth

class HomePageActivity : AppCompatActivity() {

    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var selectorFragment: Fragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_page)

        bottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigationView.setOnNavigationItemSelectedListener {

            when(it.itemId){
               R.id.home -> {
                   selectorFragment = HomeFragment()
               }
               R.id.search -> {
                   selectorFragment = SearchFragment()
               }
               R.id.add -> {
                   startActivity(Intent(this, PostActivity::class.java))
               }
               R.id.fav -> {
                   selectorFragment = NotificationFragment()
               }
               R.id.person -> {
                   selectorFragment = ProfileFragment()
               }
            }
            supportFragmentManager.beginTransaction().replace(R.id.fragment_container, selectorFragment).commit()

            return@setOnNavigationItemSelectedListener true
        }

        //For setting home fragment as default fragment.
        supportFragmentManager.beginTransaction().replace(R.id.fragment_container, HomeFragment()).commit()

    }

    override fun onBackPressed() {
        super.onBackPressed()
        FirebaseAuth.getInstance().signOut()
        startActivity(Intent(this, Login::class.java))
    }

}