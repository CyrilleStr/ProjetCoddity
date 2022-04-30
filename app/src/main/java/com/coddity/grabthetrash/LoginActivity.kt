package com.coddity.grabthetrash

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.AuthFailureError
import com.android.volley.VolleyLog
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.textfield.TextInputEditText
import org.json.JSONObject
import java.io.UnsupportedEncodingException
import kotlin.reflect.typeOf


class LoginActivity : AppCompatActivity() {

    private var authorized:Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        /** Login form function **/
        var button = findViewById<Button>(R.id.loginButton)
        var text = findViewById<TextView>(R.id.textView)

        button.setOnClickListener {
            login()
        }
    }

    fun login() {
        val usernameInput = findViewById<TextInputEditText>(R.id.username_input)
        val passwordInput = findViewById<TextInputEditText>(R.id.password_input)


        if (usernameInput.text.toString() == "" || passwordInput.text.toString() == "") {
            println("Username Should not be empty")
            showinfo("Username Should not be empty")
        } else {

            /* Request declaration */
            val loginRequest = object : StringRequest(
                Method.POST, "https://projetcoddityserverside.herokuapp.com/auth/token/",
                com.android.volley.Response.Listener { response ->
                    Log.d("response",response[1].toString())

                    if (response != null) {
                        showinfo(response.toString())
                        /* Start activites */
                        authorized = true
                        startActivity(Intent(this, HomeActivity::class.java))
                        finish()
                    }
                }, com.android.volley.Response.ErrorListener { error ->
                    showinfo("Invalid credentials")
                }) {

                @Throws(AuthFailureError::class)
                override fun getBody(): ByteArray? {
                    /* Get and format data body request */
                    val credentials = JSONObject()
                    credentials.put("username", usernameInput.text.toString())
                    credentials.put("password", passwordInput.text.toString())
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
    }

    fun showinfo(msg:String?){
        println("inside showInfo")
        Toast.makeText(applicationContext,"$msg", Toast.LENGTH_LONG).show()
    }
}