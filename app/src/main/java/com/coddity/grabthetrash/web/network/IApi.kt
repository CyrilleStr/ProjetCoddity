package com.coddity.grabthetrash.web.network


import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.*

interface IApi {
    @Headers(
        "Authorization: Token 018e09e6e5c2030878bfedfeb021f34fd982036c"
    )
    @Multipart
    @POST("grabthetrash/add-garbage/")
    fun uploadImage(@Part image:MultipartBody.Part):Call<Result>
}