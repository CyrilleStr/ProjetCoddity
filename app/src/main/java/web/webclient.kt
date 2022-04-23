package web

import android.widget.Toast
import okhttp3.*
import java.io.IOException
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

class PostString {
    private val client = OkHttpClient()

    fun run():String {
        val postBody = """
        {
            "username":"sosacy",
            "password":"calottedetesmorts"
        }""".trimMargin()

        val request = Request.Builder()
            .url("https://projetcoddityserverside.herokuapp.com/auth/token/")
            .addHeader("username", "sosacy")
            .addHeader("password", "calottedetesmorts")
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw IOException("Unexpected code $response")

            println(response.body!!.string())
            var text = response.body!!.string()
            return text
        }
        return "Erreur"
    }

    fun post():String{

        val okHttpClient = OkHttpClient()
        val request = Request.Builder()
            .url("https://projetcoddityserverside.herokuapp.com/auth/token/")
            .addHeader("username", "sosacy")
            .addHeader("password", "calottedetesmorts")
            .build()

        var responseText = ""

        okHttpClient.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                // Handle this
                responseText = "erreur"
            }

            override fun onResponse(call: Call, response: Response) {
                // Handle this
                responseText = "ok"
            }
        })
        return responseText
    }

    fun POST(url: String, parameters: HashMap<String, String>, callback: Callback): Call {
        val builder = FormBody.Builder()
        val it = parameters.entries.iterator()
        while (it.hasNext()) {
            val pair = it.next() as Map.Entry<*, *>
            builder.add(pair.key.toString(), pair.value.toString())
        }

        val formBody = builder.build()
        val request = Request.Builder()
            .url(url)
            .post(formBody)
            .build()


        val call = client.newCall(request)
        call.enqueue(callback)
        return call
    }

    fun authenticate(url: String, parameters: HashMap<String, String>): Call {
        val builder = FormBody.Builder()
        val it = parameters.entries.iterator()
        while (it.hasNext()) {
            val pair = it.next() as Map.Entry<*, *>
            builder.add(pair.key.toString(), pair.value.toString())
        }

        val formBody = builder.build()
        val request = Request.Builder()
            .url(url)
            .post(formBody)
            .build()


        val call = client.newCall(request)
        //println(request)


        client.newCall(request).enqueue(object: Callback {
            override fun onResponse(call: Call, response: Response) {
                var responseBody = response?.body?.string()
                println("Response body:")
                println(responseBody)
                println("\n\nResponse code:")
                println(response.code)
                when(response.code){
                    200 ->{
                        val jsonObject = JSONObject(responseBody)
                        //println("test ${jsonObject.get("expires_in")}")
                    }
                    400 ->{
                        val jsonObject = JSONObject(responseBody)
                        //println(jsonObject.get("error_description"))
                    }
                }

            }

            override fun onFailure(call: Call, e: IOException) {
                println("Failed to execute $e")
            }
        })
        return call
    }

    companion object {
        val MEDIA_TYPE_MARKDOWN = "application/json; charset=utf-8".toMediaType()
    }

    fun autohMethod(){
        println("apiservice.authMethod has called.............................")
    }
}

fun main() {
    PostString().run()
}