package com.coddity.grabthetrash

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView


class MainActivity : AppCompatActivity() {

    private val SHARED_PREF_USER_INFO = "SHARED_PREF_USER_INFO"

    object KeyPreferences{
        const val PICTURE_IS_TAKEN = "PICTURE_IS_TAKEN_KEY"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        startActivity(Intent(this, LoginActivity::class.java))

        getSharedPreferences(SHARED_PREF_USER_INFO, MODE_PRIVATE).edit().putInt(KeyPreferences.PICTURE_IS_TAKEN, 0).apply()
        startActivity(Intent(applicationContext, LoginActivity::class.java))
    }
}