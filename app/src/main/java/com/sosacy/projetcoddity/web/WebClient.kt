package com.sosacy.projetcoddity.web

import android.content.Context
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.*
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject
import java.io.UnsupportedEncodingException
import com.android.volley.Response.Listener as VolleyListener

/**
 * Interface between client and server
 * Use WebClient methods to send request to the server
 *
 * @constructor
 * Initialize context
 *
 * @param context_p application context using this interface
 */
class WebClient(context_p: Context) {

    private val domainName = "https://projectcoddityserverside.herokuapp.com/"
    private val applicationPath = "grabthetrash/"
    private var token: String? = null
    private var userId: String? = null
    private var applicationContext: Context = context_p

    init {
        /* Get authentication token */
        this.token =
            this.applicationContext?.getSharedPreferences(
                "SHARED_PREF_USER",
                AppCompatActivity.MODE_PRIVATE
            )
                ?.getString("SHARED_PREF_USER_TOKEN", null)
        this.userId =
            this.applicationContext?.getSharedPreferences(
                "SHARED_PREF_USER",
                AppCompatActivity.MODE_PRIVATE
            )
                ?.getString("SHARED_PREF_USER_ID", null)
    }

    /**
     * Get user thrown garbages
     *
     * @param responseListener
     */
    fun getGarbagesThrown(responseListener: VolleyListener<String?>) {
        getObjectRequest(responseListener, "get-garbage-thrown/")
    }

    /**
     * Get user garbages to throw
     *
     * @param responseListener
     */
    fun getGarbagesToThrow(responseListener: VolleyListener<String?>) {
        getObjectRequest(responseListener, "get-garbages-to-throw/")
    }

    /**
     * Get other user garbages to be rated by the current user
     *
     * @param responseListener
     */
    fun getGarbagesToRate(responseListener: VolleyListener<String?>) {
        getObjectRequest(responseListener, "garbages-to-rate/")
    }

    /**
     * Get validated bin coordinates to display on the map
     *
     * @param responseListener
     */
    fun getBinCoordinates(responseListener: VolleyListener<String?>) {
        getObjectRequest(responseListener, "get-coordinates/")
    }

    /**
     * Notify the server a garbages has been thrown
     *
     * @param garbageId
     * @param responseListener
     */
    fun throwGarbage(
        garbageId: Int,
        responseListener: Response.Listener<String>
    ) {
        val body = JSONObject()
        body.put("garbage_id", garbageId.toString())
        addObjectRequest(
            body, "throw-garbage/", responseListener,
            {
                println("error is: $it")
            },
            applicationContext
        )
    }

    /**
     * Communicate user rate for a specific garbage
     *
     * @param garbageId
     * @param note
     * @param responseListener
     */
    fun rateGarbage(
        garbageId: Int,
        note:Int,
        responseListener: Response.Listener<String>
    ) {
        val body = JSONObject()
        body.put("garbage_id", garbageId.toString())
        body.put("note", note.toString())
        addObjectRequest(
            body, "rate-garbage/", responseListener,
            {
                println("error is: $it")
            },
            applicationContext
        )
    }

    /**
     * Add a new garbage to the server
     *
     * @param latitude
     * @param longitude
     * @param responseListener
     * @param errorListener
     */
    fun addGarbage(
        latitude: String,
        longitude: String,
        responseListener: Response.Listener<String>,
        errorListener: Response.ErrorListener
    ) {
        var body = JSONObject()
        body.put("owner", userId)
        body.put("latitude", latitude)
        body.put("longitude", longitude)
        /* Make request */
        addObjectRequest(body, "add-garbage/", responseListener, errorListener, applicationContext)
    }

    /**
     * Add a new bin to the server
     *
     * @param latitude
     * @param longitude
     * @param responseListener
     * @param errorListener
     */
    fun addBin(
        latitude: String,
        longitude: String,
        responseListener: Response.Listener<String>,
        errorListener: Response.ErrorListener
    ) {
        val body = JSONObject()
        body.put("owner", userId)
        body.put("latitude", latitude)
        body.put("longitude", longitude)
        /* Make request */
        addObjectRequest(body, "add-bin/", responseListener, errorListener, applicationContext)
    }

    /**
     * Get authentication token
     *
     * @param credentials username and password in JSON obejct
     * @param responseListener on success callback
     * @param errorListener on error callback
     */
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
        val requestQueue = Volley.newRequestQueue(this.applicationContext)
        requestQueue.add(request)
    }

    /**
     * Make a HTTP GET request to the server
     *
     * @param responseListener on success callback
     * @param path end url
     */
    private fun getObjectRequest(responseListener: VolleyListener<String?>, path: String) {

        /* Prepare request */
        val request = object : StringRequest(
            com.android.volley.Request.Method.GET,
            domainName + applicationPath + path,
            responseListener,
            Response.ErrorListener {
                println("error is: $it")
            }) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String>? {
                /* Format header : content type + authorization token */
                val headers: HashMap<String, String> = HashMap()
                headers["Content-Type"] = "application/json"
                headers["Authorization"] = "Token $token"
                return headers
            }
        }
        Log.d("request", request.toString())

        /* Execute request */
        val requestQueue = Volley.newRequestQueue(this.applicationContext)
        requestQueue.add(request)
    }

    /**
     * Make a HTTP POST request to the server
     *
     * @param body body post request
     * @param path end url
     * @param responseListener on success callback
     * @param errorListener on error callback
     * @param applicationContext
     */
    private fun addObjectRequest(
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
        val requestQueue = Volley.newRequestQueue(this.applicationContext)
        requestQueue.add(request)
    }
}