package com.coddity.grabthetrash.web.network

import android.util.Log
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

abstract class ServiceBuilder {
    companion object {

        val myApi: IApi by lazy {
            return@lazy getRetrofit().create(IApi::class.java)
        }
        @Volatile
        private var INSTANCE: Retrofit? = null

        private fun getRetrofit(): Retrofit {

            val temp = INSTANCE
            if (temp != null) {
                return temp
            }
            synchronized(this) {
                val instance = Retrofit.Builder()
                    .baseUrl("https://projetcoddityserverside.herokuapp.com/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(OkHttpClient.Builder().build())
                    .build()
                INSTANCE = instance
                return instance
            }
        }
    }
}