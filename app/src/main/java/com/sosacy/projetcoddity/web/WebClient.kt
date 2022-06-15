package com.sosacy.projetcoddity.web

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.*
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.Headers
import com.github.kittinunf.fuel.gson.jsonBody
import org.json.JSONException
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.UnsupportedEncodingException
import com.android.volley.Response.Listener as VolleyListener
import com.github.kittinunf.fuel.httpGet
import com.google.gson.Gson

/**
 * Interface between client and server
 */
class WebClient(context_p: Context) {

    private val domainName = "https://projectcoddityserverside.herokuapp.com/"
    private val applicationPath = "grabthetrash/"
    private var imageData: ByteArray? = null
    private var token: String? = null
    private var userId: String? = null
    private var contextApp: Context = context_p

    init {
        /* Get token */
        this.token =
            this.contextApp?.getSharedPreferences(
                "SHARED_PREF_USER",
                AppCompatActivity.MODE_PRIVATE
            )
                ?.getString("SHARED_PREF_USER_TOKEN", null)
        this.userId =
            this.contextApp?.getSharedPreferences(
                "SHARED_PREF_USER",
                AppCompatActivity.MODE_PRIVATE
            )
                ?.getString("SHARED_PREF_USER_ID", null)
    }

    fun getBinCoordinates(responseListener: VolleyListener<String?>) {

        /* Prepare request */
        val request = object : StringRequest(
            com.android.volley.Request.Method.GET,
            domainName + applicationPath + "get-coordinates/",
            responseListener,
            Response.ErrorListener {
                println("error is: $it")
            }) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String>? {
                /* Format header : application/json */
                val headers: HashMap<String, String> = HashMap()
                headers["Content-Type"] = "application/json"
                headers["Authorization"] = "Token $token"
                return headers
            }
        }
        Log.d("request", request.toString())

        /* Execute request */
        val requestQueue = Volley.newRequestQueue(this.contextApp)
        requestQueue.add(request)
    }

    fun authenticate(
        credentials: JSONObject,
        responseListener: VolleyListener<String?>,
        errorListener: Response.ErrorListener
    ) {
        /* Prepare request */
        val request = object : StringRequest(
            com.android.volley.Request.Method.POST,
            domainName + "auth/token/",
            responseListener,
            errorListener
        ) {
            @Throws(AuthFailureError::class)
            override fun getBody(): ByteArray? {
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
        Log.d("request", request.toString())

        /* Execute request */
        val requestQueue = Volley.newRequestQueue(this.contextApp)
        requestQueue.add(request)
    }

    fun addGarbage(
        latitude: String,
        longitude: String,
        responseListener: Response.Listener<String>,
        errorListener: Response.ErrorListener,
        applicationContext: Context
    ) {
        var body = JSONObject()
        body.put("owner", userId)
        body.put("latitude", latitude)
        body.put("longitude", longitude)
        /* Make request */
        addObjectRequest(body, "add-garbage/", responseListener, errorListener, applicationContext)
    }

    fun addBin(
        latitude: String,
        longitude: String,
        responseListener: Response.Listener<String>,
        errorListener: Response.ErrorListener,
        applicationContext: Context
    ) {
        val body = JSONObject()
        body.put("owner", userId)
        body.put("latitude", latitude)
        body.put("longitude", longitude)
        /* Make request */
        addObjectRequest(body, "add-bin/", responseListener, errorListener, applicationContext)
    }

    fun addObjectRequest(
        body: JSONObject,
        path: String,
        responseListener: Response.Listener<String>,
        errorListener: Response.ErrorListener,
        applicationContext: Context
    ) {
        /* Prepare request */
        val request = object : StringRequest(
            com.android.volley.Request.Method.POST,
            domainName + applicationPath + path,
            responseListener,
            errorListener
        ) {
            @Throws(AuthFailureError::class)
            override fun getBody(): ByteArray? {
                return try {
                    body.toString().toByteArray(charset("utf-8"))
                } catch (e: UnsupportedEncodingException) {
                    e.printStackTrace()
                    VolleyLog.wtf(
                        "Unsupported Encoding while trying to get the bytes of %s using %s",
                        body.toString(),
                        "utf-8"
                    )
                    null
                }
            }

            override fun getHeaders(): Map<String, String>? {
                /* Format header : application/json */
                val headers: HashMap<String, String> = HashMap()
                headers["Content-Type"] = "application/json"
                headers["Authorization"] = "token $token"
                return headers
            }
        }
        Log.d("request", request.toString())

        /* Execute request */
        val requestQueue = Volley.newRequestQueue(this.contextApp)
        requestQueue.add(request)
    }
}