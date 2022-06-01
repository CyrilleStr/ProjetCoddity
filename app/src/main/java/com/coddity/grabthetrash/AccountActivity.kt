package com.coddity.grabthetrash

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import android.widget.Button
import android.widget.TextView
import android.widget.Toast

class AccountActivity : AppCompatActivity() {

    private val SHARED_PREF_USER_INFO = "SHARED_PREF_USER_INFO"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account)

        if (getSharedPreferences(SHARED_PREF_USER_INFO, MODE_PRIVATE).getInt(HomeActivity.KeyPreferences.TRASH_ON_THE_WAY, 1)==1) {
            startService(Intent(this, BackgroundLocationUpdateService::class.java))
        }else{
            Log.e("TRASH_ON_THE_WAY","no")
        }

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

    fun showinfo(msg:String){
        println("inside showInfo")
        Toast.makeText(applicationContext,"$msg",Toast.LENGTH_SHORT).show()
    }
}