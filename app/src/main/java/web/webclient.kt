package web

import android.content.Context
import android.util.Log
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.VolleyLog
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject
import java.io.IOException
import java.io.UnsupportedEncodingException
import javax.xml.transform.ErrorListener
import com.android.volley.Response.Listener as VolleyListener


class WebClient {
    private val domainName = "https://projetcoddityserverside.herokuapp.com/"


    fun authenticate(credentials: JSONObject, responseListener: VolleyListener<String?>,errorListener:Response.ErrorListener, applicationContext:Context) {
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
        val requestQueue = Volley.newRequestQueue(applicationContext)
        requestQueue.add(request)
    }
}