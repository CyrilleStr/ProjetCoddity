package com.coddity.grabthetrash.web

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

    fun uploadImage(/*picturePath: String,*/ itemAttributes: MutableMap<String,String>, photo: Bitmap/*, responseListener: VolleyListener<String?>, errorListener:Response.ErrorListener*/) {

        /* Image formatting: convert Bitmap to ByteArray */
        val stream = ByteArrayOutputStream()
        photo.compress(Bitmap.CompressFormat.JPEG, 80, stream)
        val imageData = stream.toByteArray()
//        photo.recycle()
        if(imageData == null){
            Log.d("Upload file aborting","no imageData")
            return
        }

        // Image
//        val bm = BitmapFactory.decodeFile(picturePath)
//        val bao = ByteArrayOutputStream()
//        bm.compress(Bitmap.CompressFormat.JPEG, 90, bao)
//        val ba = bao.toByteArray()
//        var ba1 = Base64.encodeToString(ba, Base64.NO_WRAP)
//        val size: Int = photo.getRowBytes() * photo.getHeight()
//        val byteBuffer: ByteBuffer = ByteBuffer.allocate(size)
//        photo.copyPixelsToBuffer(byteBuffer)
//        imageData = byteBuffer.array()

        /* Prepare datapart request */
        val request = object : VolleyFileUploadRequest(
            Request.Method.POST,
            domainName + applicationPath + "add-garbage/",
            Response.Listener {
                Log.d("ResponseListener","$it")
            },
            Response.ErrorListener {
                parseVolleyError(it)
            }
        ) {
            override fun getParams(): MutableMap<String, String>? {
                return itemAttributes
            }
            override fun getByteData(): MutableMap<String, FileDataPart> {
                var itemAttributesFile : MutableMap<String,FileDataPart> = HashMap()
                itemAttributesFile["image"] = FileDataPart("image", getFileDataFromDrawable(photo)!!, "image/png")
                Log.d("params",itemAttributesFile.toString())
                return itemAttributesFile
            }
            @Throws(AuthFailureError::class)
            override fun getHeaders(): MutableMap<String, String> {
                /* Format header */
                var headers : MutableMap<String, String> = HashMap()
                headers["Authorization"] = "Token $token"
                headers["Content-Type"] = super.getBodyContentType()
                return headers
            }
        }
//        Log.d("request",request; toString())

        /* Execute Request*/
        Volley.newRequestQueue(contextApp).add(request)
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