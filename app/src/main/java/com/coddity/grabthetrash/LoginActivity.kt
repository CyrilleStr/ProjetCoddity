package com.coddity.grabthetrash

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import org.json.JSONObject
import com.coddity.grabthetrash.web.WebClient


class LoginActivity : AppCompatActivity() {

    private var username:String? = null;
    private var password:String? = null;
    private val SHARED_PREF_USER = "SHARED_PREF_USER"
    private val SHARED_PREF_USER_TOKEN = "SHARED_PREF_USER_TOKEN"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        /** Login form function **/
        var button = findViewById<Button>(R.id.loginButton)

        button.setOnClickListener {
            if(getInput())
                login()
        }

        /* Get authentication Token */
        val token = getSharedPreferences(SHARED_PREF_USER, MODE_PRIVATE).getString(SHARED_PREF_USER_TOKEN, null)
        Log.d("token",token.toString())
        if (token != null) {
            Log.d("token",token)
            /* Start activities */
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }
    }

    /**
     * Get user input
     *
     * @return true if valid input
     * @return false if not
     */
    private fun getInput():Boolean{
        val usernameInput = findViewById<TextInputEditText>(R.id.username_input)
        val passwordInput = findViewById<TextInputEditText>(R.id.password_input)

        if (usernameInput.text.toString() == "" || passwordInput.text.toString() == "") {
            println("Username Should not be empty")
            showMsg("Username Should not be empty")
            return false
        }else{
            username = usernameInput.text.toString()
            password = passwordInput.text.toString()
        }
        return true
    }

    /**
     * Get distant server authentication token
     */
    private fun login(){
        Log.d("debug","login")

        /* Format credentials */
        val credentials = JSONObject()
        credentials.put("username", username)
        credentials.put("password", password)

        WebClient(applicationContext).authenticate(
            credentials,
            { response ->
                Log.d("response",response.toString())
                if (response != null) {
                    /* Get token authentication */
                    val jsonObj: JSONObject = JSONObject(response.toString())
                    var token = jsonObj.get("token").toString()
                    Log.d("token",token)
                    showMsg("Authentication successful")

                    /* Store authentication token */
                    val preferences = getSharedPreferences(SHARED_PREF_USER, MODE_PRIVATE)
                    val editor = preferences.edit()
                    editor.putString(SHARED_PREF_USER_TOKEN, token)
                    editor.apply()

                    /* Start activities */
                    startActivity(Intent(this, HomeActivity::class.java))
                    finish()
                }
            },
            { error ->
                showMsg("Invalid credentials")
            })
    }

    /**
     * Show the user a message
     * @param msg the message to show
     */
    fun showMsg(msg:String?){
        Toast.makeText(applicationContext,"$msg", Toast.LENGTH_LONG).show()
    }
}