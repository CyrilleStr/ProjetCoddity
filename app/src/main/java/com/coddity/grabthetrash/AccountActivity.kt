package com.coddity.grabthetrash

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class AccountActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account)

        /** Setup bottom navigation **/
        // Get View
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        // Set Home selected
        bottomNavigationView.selectedItemId = R.id.navigation_account
        // Perform item selected listener
        bottomNavigationView.setOnNavigationItemSelectedListener(BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    startActivity(Intent(applicationContext,HomeActivity::class.java))
                    overridePendingTransition(0, 0)
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_account -> return@OnNavigationItemSelectedListener true
                R.id.maps -> {
                    startActivity(Intent(applicationContext,MapsActivity::class.java))
                    overridePendingTransition(0, 0)
                    return@OnNavigationItemSelectedListener true
                }
            }
            false
        })
    }
}