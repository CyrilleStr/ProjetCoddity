package com.coddity.grabthetrash

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import web.PostString

class AccountActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account)

        /** Login form function **/
        var button = findViewById<Button>(R.id.loginButton)
        var text = findViewById<TextView>(R.id.textView)

        button.setOnClickListener {
            //text.text = client2.post()
            login()
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

    fun login():Unit{
        val username = "a"//username.text
        val passwd = "a"//password.text
        if(username.isNullOrEmpty()){
            println("Username Should not be empty")
            showinfo("Username Should not be empty")

        } else{
            val url = "https://projetcoddityserverside.herokuapp.com/auth/token"
            val map: HashMap<String, String> = hashMapOf("username" to "sosacy", "password" to "calottedetesmorts")

            PostString().authenticate(url, map)

            val i = Intent(this,HomeActivity::class.java)
            startActivity(i)
            finish()
        }
    }

    fun showinfo(msg:String){
        println("inside showInfo")
        Toast.makeText(applicationContext,"$msg",Toast.LENGTH_SHORT).show()
    }
}