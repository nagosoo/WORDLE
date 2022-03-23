package com.example.wordle.retrofit

import com.example.wordle.Constant
import okhttp3.Interceptor
import okhttp3.Response

class AuthorizationInterceptor : Interceptor{
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
            .newBuilder()
            .addHeader("key", Constant.API_KEY)
            .build()
        return chain.proceed(request)
    }
}