package com.sosacy.projetcoddity.web

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.*
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.UnsupportedEncodingException
import com.android.volley.Response.Listener as VolleyListener


/**
 * Interface between client and server
 */
class WebClient (context_p:Context){

    private val domainName = "https://projetcoddityserverside.herokuapp.com/"
    private val applicationPath = "grabthetrash/"
    private var imageData: ByteArray? = null
    private var token:String? = null
    private var contextApp:Context = context_p

    init{
        /* Get token */
        this.token =
            this.contextApp?.getSharedPreferences("SHARED_PREF_USER", AppCompatActivity.MODE_PRIVATE)
                ?.getString("SHARED_PREF_USER_TOKEN", null)
        println(token)
    }

    fun getBinCoordinates (responseListener: VolleyListener<String?>){

        /* Prepare request */
        val request = object : StringRequest(
            com.android.volley.Request.Method.GET,
            domainName + applicationPath + "get-coordinates/",
            responseListener,
            Response.ErrorListener {
                println("error is: $it")
            }){
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String>? {
                /* Format header : application/json */
                val headers: HashMap<String, String> = HashMap()
                headers["Content-Type"] = "application/json"
                headers["Authorization"] = "Token $token"
                return headers
            }
        }
        Log.d("request",request.toString())

        /* Execute request */
        val requestQueue = Volley.newRequestQueue(this.contextApp)
        requestQueue.add(request)
    }

    fun authenticate(credentials: JSONObject, responseListener: VolleyListener<String?>,errorListener:Response.ErrorListener) {
        /* Prepare request */
        val request = object : StringRequest(
          com.android.volley.Request.Method.POST,
            domainName + "auth/token/",
            responseListener,
            errorListener){
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
        Log.d("request",request.toString())

        /* Execute request */
        val requestQueue = Volley.newRequestQueue(this.contextApp)
        requestQueue.add(request)
    }

    fun addGarbage(photo: Bitmap, garbage: JSONObject, responseListener: VolleyListener<String?>, errorListener:Response.ErrorListener, applicationContext:Context){
        /* Prepare request */
        val request = object : StringRequest(
            com.android.volley.Request.Method.POST,
            domainName + "grabthetrash/add-garbage/",
            responseListener,
            errorListener){
            @Throws(AuthFailureError::class)
            override fun getBody(): ByteArray? {
                return try {
                    garbage.toString().toByteArray(charset("utf-8"))
                } catch (e: UnsupportedEncodingException) {
                    e.printStackTrace()
                    VolleyLog.wtf(
                        "Unsupported Encoding while trying to get the bytes of %s using %s",
                        garbage.toString(),
                        "utf-8"
                    )
                    null
                }
            }
            override fun getHeaders(): Map<String, String>? {
                /* Format header : application/json */
                val headers: HashMap<String, String> = HashMap()
                headers["Content-Type"] = "multipart/form-data"
                return headers
            }
        }
        Log.d("request",request.toString())

        /* Execute request */
        val requestQueue = Volley.newRequestQueue(applicationContext)
        requestQueue.add(request)
    }
    fun parseVolleyError(error: VolleyError) {
        try {
//            val responseBody:String = String(error.networkResponse.data, charset("utf-8")
            val data = JSONObject(error.networkResponse.data.toString())
            val errors = data.getJSONArray("errors")
            val jsonMessage = errors.getJSONObject(0)
            val message = jsonMessage.getString("message")
            Log.d("error request",message)
        } catch (e: JSONException) {
        } catch (errorr: UnsupportedEncodingException) {
        }
    }
    fun getFileDataFromDrawable(bitmap: Bitmap): ByteArray? {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 80, byteArrayOutputStream)
        return byteArrayOutputStream.toByteArray()
    }
}