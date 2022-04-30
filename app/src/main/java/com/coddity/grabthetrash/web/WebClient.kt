package com.coddity.grabthetrash.web

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
 */
class WebClient (context_p:Context){

    private val domainName = "https://projetcoddityserverside.herokuapp.com/"
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
            domainName + "grabthetrash/get-coordinates/",
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

//    @Throws(IOException::class)
//    private fun createImageData(uri: Uri) {
//        val inputStream = contentResolver.openInputStream(uri)
//        inputStream?.buffered()?.use {
//            imageData = it.readBytes()
//        }
//    }
//
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        if (resultCode == Activity.RESULT_OK && requestCode == IMAGE_PICK_CODE) {
//            val uri = data?.data
//            if (uri != null) {
//                imageView.setImageURI(uri)
//                createImageData(uri)
//            }
//        }
//        super.onActivityResult(requestCode, resultCode, data)
//    }
//
//    private fun uploadImage(photo: Bitmap, garbage: HashMap<String,FileDataPart>, responseListener: VolleyListener<String?>, errorListener:Response.ErrorListener, applicationContext:Context) {
//        /* Image formatting */
//        imageData?: return
//        val inputStream = contentResolver.openInputStream(uri)
//        inputStream?.buffered()?.use {
//            imageData = it.readBytes()
//        }
//
//        /* Prepare datapart request */
//        val request = object : VolleyFileUploadRequest(
//            Request.Method.POST,
//            domainName + "grabthetrash/add-garbage/",
//            Response.Listener {
//                println("response is: $it")
//            },
//            Response.ErrorListener {
//                println("error is: $it")
//            }
//        ) {
//            override fun getByteData(): MutableMap<String, FileDataPart> {
//                var params = HashMap<String, FileDataPart>()
//                params["image"] = FileDataPart("image", imageData!!, "jpeg")
//                return params
//            }
//        }
//        /* Execute Request*/
//        Volley.newRequestQueue(applicationContext).add(request)
//    }
//
//
//
//    fun addGarbage(photo: Bitmap, garbage: JSONObject, responseListener: VolleyListener<String?>, errorListener:Response.ErrorListener, applicationContext:Context){
//        /* Prepare request */
//        val request = object : StringRequest(
//            com.android.volley.Request.Method.POST,
//            domainName + "grabthetrash/add-garbage/",
//            responseListener,
//            errorListener){
//            @Throws(AuthFailureError::class)
//            override fun getBody(): ByteArray? {
//                return try {
//                    garbage.toString().toByteArray(charset("utf-8"))
//                } catch (e: UnsupportedEncodingException) {
//                    e.printStackTrace()
//                    VolleyLog.wtf(
//                        "Unsupported Encoding while trying to get the bytes of %s using %s",
//                        garbage.toString(),
//                        "utf-8"
//                    )
//                    null
//                }
//            }
//            override fun getHeaders(): Map<String, String>? {
//                /* Format header : application/json */
//                val headers: HashMap<String, String> = HashMap()
//                headers["Content-Type"] = "multipart/form-data"
//                return headers
//            }
//        }
//        Log.d("request",request.toString())
//
//        /* Execute request */
//        val requestQueue = Volley.newRequestQueue(applicationContext)
//        requestQueue.add(request)
//    }
}