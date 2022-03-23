package com.example.wordle.retrofit

import android.util.Xml
import com.example.wordle.Constant
import retrofit2.Response
import retrofit2.http.GET

interface RetrofitService {
    val key: String
        get() = Constant.API_KEY

    @GET("search/?key=140962EC161BEC467D71B50D389DAB90")
    suspend fun getTestWords(): Response<Xml>
}