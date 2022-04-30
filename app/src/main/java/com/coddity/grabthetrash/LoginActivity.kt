package com.coddity.grabthetrash

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.AuthFailureError
import com.android.volley.VolleyLog
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.textfield.TextInputEditText
import org.json.JSONObject
import java.io.UnsupportedEncodingException


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
        val token = getSharedPreferences(SHARED_PREF_USER, MODE_PRIVATE).getString(
            SHARED_PREF_USER_TOKEN,
            null
        )

        if(token != null){
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
    private fun login() {

        /* Request declaration */
        val loginRequest = object : StringRequest(
            Method.POST, "https://projetcoddityserverside.herokuapp.com/auth/token/",
            com.android.volley.Response.Listener { response ->
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
            }, com.android.volley.Response.ErrorListener { error ->
                showMsg("Invalid credentials")
            }) {

            @Throws(AuthFailureError::class)
            override fun getBody(): ByteArray? {
                /* Get and format data body request */
                val credentials = JSONObject()
                credentials.put("username", username)
                credentials.put("password", password)
                return try {
                    credentials.toString().toByteArray(charset("utf-8"))
                } catch (e: UnsupportedEncodingException) {
                    e.printStackTrace()
                    VolleyLog.wtf(
                        "Unsupported Encoding while trying to get the bytes of %s using %s",
                        credentials.toString(),
                        "utf-8"
                    )
                    null
                }
            }
            override fun getHeaders(): Map<String, String>? {
                /* Format header : application/json */
                val headers: HashMap<String, String> = HashMap()
                headers["Content-Type"] = "application/json"
                return headers
            }
        }
        /* Execute request */
        val requestQueue = Volley.newRequestQueue(applicationContext)
        requestQueue.add(loginRequest)

    }

    /**
     * Show the user a message
     * @param msg the message to show
     */
    fun showMsg(msg:String?){
        Toast.makeText(applicationContext,"$msg", Toast.LENGTH_LONG).show()
    }
}